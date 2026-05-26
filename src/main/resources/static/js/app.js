const API = '/api';
let currentGroupId = null;
let members = [];

// ── GROUPS ──────────────────────────────────────────────
async function loadGroups() {
    const res = await fetch(`${API}/groups`);
    const groups = await res.json();
    const el = document.getElementById('groups-list');
    if (groups.length === 0) {
        el.innerHTML = '<p style="color:#718096;font-size:0.9rem">No groups yet. Create one above.</p>';
        return;
    }
    el.innerHTML = groups.map(g => `
        <div class="list-item group-item">
            <span>📁 ${g.name} <span class="tag">${g.expenses ? g.expenses.length : 0} expenses</span></span>
            <button class="btn-sm" onclick="openGroup(${g.id}, '${g.name}')">Open</button>
        </div>`).join('');
}

async function createGroup() {
    const name = document.getElementById('group-name').value.trim();
    if (!name) return alert('Enter a group name');
    await fetch(`${API}/groups`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    });
    document.getElementById('group-name').value = '';
    loadGroups();
}

function openGroup(id, name) {
    currentGroupId = id;
    document.getElementById('active-group-label').textContent = `Active: ${name}`;
    document.getElementById('main-app').style.display = 'block';
    document.getElementById('group-section').style.display = 'none';
    loadMembers();
    loadExpenses();
    clearResults();
}

function closeGroup() {
    currentGroupId = null;
    members = [];
    document.getElementById('main-app').style.display = 'none';
    document.getElementById('group-section').style.display = 'block';
    loadGroups();
}

function clearResults() {
    document.getElementById('balances-list').innerHTML = '';
    document.getElementById('settlements-list').innerHTML = '';
    document.getElementById('ai-result').textContent = '';
}

// ── MEMBERS ─────────────────────────────────────────────
async function loadMembers() {
    const res = await fetch(`${API}/groups/${currentGroupId}`);
    const group = await res.json();
    members = group.members || [];
    renderMembers();
    renderMemberDropdowns();
}

function renderMembers() {
    const el = document.getElementById('members-list');
    if (members.length === 0) {
        el.innerHTML = '<p style="color:#718096;font-size:0.9rem">No members yet.</p>';
        return;
    }
    el.innerHTML = members.map(m => `
        <div class="list-item">👤 ${m.name}</div>`).join('');
}

function renderMemberDropdowns() {
    const paidBy = document.getElementById('exp-paid-by');
    paidBy.innerHTML = '<option value="">-- Paid By --</option>' +
        members.map(m => `<option value="${m.id}">${m.name}</option>`).join('');

    const boxes = document.getElementById('split-checkboxes');
    boxes.innerHTML = members.length > 0
        ? '<strong style="width:100%;font-size:0.85rem">Split Among:</strong>' +
          members.map(m => `
            <label>
                <input type="checkbox" value="${m.id}" checked> ${m.name}
            </label>`).join('')
        : '';
}

async function addMember() {
    const name = document.getElementById('member-name').value.trim();
    if (!name) return alert('Enter a member name');
    await fetch(`${API}/groups/${currentGroupId}/members`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    });
    document.getElementById('member-name').value = '';
    loadMembers();
}

// ── EXPENSES ─────────────────────────────────────────────
async function addExpense() {
    const description = document.getElementById('exp-desc').value.trim();
    const amount = document.getElementById('exp-amount').value;
    const category = document.getElementById('exp-category').value;
    const paidById = document.getElementById('exp-paid-by').value;
    const checked = [...document.querySelectorAll('#split-checkboxes input:checked')];
    const splitAmongIds = checked.map(c => parseInt(c.value));

    if (!description || !amount || !category || !paidById || splitAmongIds.length === 0) {
        return alert('Fill all fields and select at least one person to split among');
    }

    await fetch(`${API}/groups/${currentGroupId}/expenses`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ description, amount: parseFloat(amount), category, paidById: parseInt(paidById), splitAmongIds })
    });

    document.getElementById('exp-desc').value = '';
    document.getElementById('exp-amount').value = '';
    loadExpenses();
}

async function loadExpenses() {
    const res = await fetch(`${API}/groups/${currentGroupId}`);
    const group = await res.json();
    const expenses = group.expenses || [];
    const el = document.getElementById('expenses-list');
    if (expenses.length === 0) {
        el.innerHTML = '<p style="color:#718096;font-size:0.9rem">No expenses yet.</p>';
        return;
    }
    el.innerHTML = expenses.map(e => `
        <div class="list-item">
            <strong>${e.description}</strong> — ₹${e.amount.toFixed(2)}
            <span class="tag">${e.category}</span><br>
            <small>Paid by <strong>${e.paidBy.name}</strong> · Split among ${e.splitAmong.map(p => p.name).join(', ')}</small>
        </div>`).join('');
}

// ── BALANCES & SETTLEMENTS ────────────────────────────────
async function loadBalances() {
    const res = await fetch(`${API}/groups/${currentGroupId}/balances`);
    const balances = await res.json();
    const el = document.getElementById('balances-list');
    el.innerHTML = '<strong>Balances:</strong><br>' +
        Object.entries(balances).map(([name, bal]) => `
            <div class="list-item">
                👤 ${name}: <span class="${bal >= 0 ? 'balance-pos' : 'balance-neg'}">
                    ${bal >= 0 ? '+' : ''}₹${bal.toFixed(2)}
                    ${bal >= 0 ? '(gets back)' : '(owes)'}
                </span>
            </div>`).join('');
}

async function loadSettlements() {
    const res = await fetch(`${API}/groups/${currentGroupId}/settlements`);
    const settlements = await res.json();
    const el = document.getElementById('settlements-list');
    if (settlements.length === 0) {
        el.innerHTML = '<div class="list-item" style="border-left-color:#38a169">✅ All settled up!</div>';
        return;
    }
    el.innerHTML = '<strong>Settlement Plan:</strong><br>' +
        settlements.map(s => `
            <div class="list-item settlement-item">
                💸 <strong>${s.from}</strong> pays <strong>${s.to}</strong> → ₹${s.amount.toFixed(2)}
            </div>`).join('');
}

// ── AI INSIGHTS ──────────────────────────────────────────
async function getAIInsights() {
    const el = document.getElementById('ai-result');
    el.textContent = 'Thinking...';
    const res = await fetch(`${API}/groups/${currentGroupId}/ai-insights`);
    const data = await res.json();
    el.textContent = data.insight;
}

// ── INIT ─────────────────────────────────────────────────
loadGroups();
