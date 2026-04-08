document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const loginSection = document.getElementById('login-section');
    const registerSection = document.getElementById('register-section');
    const dashboardSection = document.getElementById('dashboard-section');
    const logoutBtn = document.getElementById('logout-btn');
    const toRegister = document.getElementById('to-register');
    const toLogin = document.getElementById('to-login');
    toRegister.addEventListener('click', (e) => {
        e.preventDefault();
        loginSection.classList.remove('active');
        registerSection.classList.add('active');
    });
    toLogin.addEventListener('click', (e) => {
        e.preventDefault();
        registerSection.classList.remove('active');
        loginSection.classList.add('active');
    });
    const actionForm = document.getElementById('action-form');
    const actionType = document.getElementById('action-type');
    const transferExtra = document.getElementById('transfer-extra');
    const balanceDisplay = document.getElementById('balance-display');
    const userGreeting = document.getElementById('user-greeting');
    const accNoDisplay = document.getElementById('acc-no-display');
    const accTypeDisplay = document.getElementById('acc-type-display');
    const historyBody = document.getElementById('history-body');
    function showToast(message, type = 'success') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                ${type === 'success' ? '<polyline points="20 6 9 17 4 12"></polyline>' : '<circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line>'}
            </svg>
            <span>${message}</span>
        `;
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
    actionType.addEventListener('change', () => {
        transferExtra.style.display = actionType.value === 'TRANSFER' ? 'block' : 'none';
        const submitBtn = document.getElementById('action-submit');
        submitBtn.style.background = actionType.value === 'TRANSFER' ? 'var(--success)' : (actionType.value === 'WITHDRAW' ? '#4b5563' : 'var(--primary)');
    });
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new URLSearchParams();
        formData.append('username', document.getElementById('username').value);
        formData.append('password', document.getElementById('password').value);
        try {
            const response = await fetch('/login', {
                method: 'POST',
                body: formData
            });
            if (response.ok) {
                showDashboard();
                showToast('Welcome back!');
            } else {
                showToast('Invalid credentials', 'error');
            }
        } catch (err) {
            showToast('Login failed: ' + err.message, 'error');
        }
    });
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new URLSearchParams();
        formData.append('username', document.getElementById('reg-username').value);
        formData.append('password', document.getElementById('reg-password').value);
        formData.append('holder', document.getElementById('reg-holder').value);
        formData.append('balance', document.getElementById('reg-balance').value);
        formData.append('acc_type', document.getElementById('reg-type').value);
        try {
            const response = await fetch('/register', {
                method: 'POST',
                body: formData
            });
            if (response.ok) {
                showToast('Account created! Please sign in.');
                registerSection.classList.remove('active');
                loginSection.classList.add('active');
            } else {
                const msg = await response.text();
                showToast(msg, 'error');
            }
        } catch (err) {
            showToast('Registration failed: ' + err.message, 'error');
        }
    });
    logoutBtn.addEventListener('click', async () => {
        await fetch('/logout', { method: 'POST' });
        location.reload();
    });
    async function showDashboard() {
        loginSection.classList.remove('active');
        registerSection.classList.remove('active');
        dashboardSection.classList.add('active');
        await refreshData();
    }
    async function refreshData() {
        await Promise.all([updateAccountInfo(), updateHistory()]);
    }
    async function updateAccountInfo() {
        const response = await fetch('/api/account');
        if (response.ok) {
            const acc = await response.json();
            balanceDisplay.innerText = `₹${acc.balance.toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
            userGreeting.innerText = `Hi, ${acc.holder}`;
            accNoDisplay.innerText = acc.accountNo;
            accTypeDisplay.innerText = acc.accType;
        }
    }
    async function updateHistory() {
        const response = await fetch('/api/history');
        if (response.ok) {
            const history = await response.json();
            historyBody.innerHTML = history.length ? '' : '<tr><td colspan="4" style="text-align:center">No transactions yet</td></tr>';
            history.forEach(tx => {
                const row = document.createElement('tr');
                let isIncome = tx.transType === 'DEPOSIT' || (tx.transType === 'TRANSFER' && tx.recipientName && tx.recipientName.startsWith('Transfer from'));
                const sign = isIncome ? '+' : '-';
                const badgeClass = tx.transType === 'DEPOSIT' ? 'income' : (tx.transType === 'WITHDRAWAL' ? 'expense' : 'transfer');
                let details = tx.transType;
                if (tx.transType === 'TRANSFER' && tx.recipientName) {
                    if (tx.recipientName.startsWith('Transfer from')) {
                        details = `From ${tx.recipientAcc}`;
                    } else {
                        details = `To ${tx.recipientName} (${tx.recipientAcc})`;
                    }
                }
                row.innerHTML = `
                    <td><span style="font-family:monospace; color:var(--text-muted)">#${tx.transId}</span></td>
                    <td><span class="status-badge ${badgeClass}">${details}</span></td>
                    <td class="amount-col" style="color:${sign === '+' ? 'var(--success)' : 'var(--danger)'}">
                        ${sign}₹${tx.amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </td>
                    <td style="color:var(--text-muted); font-size:13px">${new Date(tx.transTime).toLocaleString()}</td>
                `;
                historyBody.appendChild(row);
            });
        }
    }
    actionForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const type = actionType.value;
        const amount = document.getElementById('action-amount').value;
        const toAcc = document.getElementById('action-to').value;
        const formData = new URLSearchParams();
        formData.append('amount', amount);
        if (type === 'TRANSFER') formData.append('toAccount', toAcc);
        const endpoint = type === 'DEPOSIT' ? '/api/deposit' : (type === 'WITHDRAW' ? '/api/withdraw' : '/api/transfer');
        try {
            const res = await fetch(endpoint, { method: 'POST', body: formData });
            if (res.ok) {
                showToast(`${type.charAt(0) + type.slice(1).toLowerCase()} Successful!`);
                actionForm.reset();
                transferExtra.style.display = 'none';
                await refreshData();
            } else {
                const msg = await res.text();
                showToast(msg || 'Transaction failed', 'error');
            }
        } catch (err) {
            showToast('Connection error', 'error');
        }
    });
    fetch('/api/account').then(res => {
        if (res.ok) showDashboard();
    });
});