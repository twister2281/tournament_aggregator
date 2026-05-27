document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('matchForm');
    const input = document.getElementById('matchId');
    const submitButton = document.getElementById('submitButton');
    const statusBox = document.getElementById('statusBox');
    const resultBox = document.getElementById('resultBox');

    const resultMatchId = document.getElementById('resultMatchId');
    const resultRadiant = document.getElementById('resultRadiant');
    const resultDire = document.getElementById('resultDire');
    const resultDuration = document.getElementById('resultDuration');
    const resultWinner = document.getElementById('resultWinner');

    if (!form) {
        return;
    }

    function formatDuration(totalSeconds) {
        const seconds = Number(totalSeconds || 0);
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;

        if (hours > 0) {
            return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
        }
        return `${minutes}:${secs.toString().padStart(2, '0')}`;
    }

    function showStatus(message, type) {
        statusBox.textContent = message;
        statusBox.className = `alert alert-${type === 'error' ? 'danger' : 'success'}`;
        statusBox.style.display = 'block';
    }

    function clearStatus() {
        statusBox.style.display = 'none';
        statusBox.className = 'alert';
    }

    function renderResult(match) {
        resultMatchId.textContent = `#${match.matchId ?? '—'}`;
        resultRadiant.textContent = match.radiantTeamName ?? 'Неизвестно';
        resultDire.textContent = match.direTeamName ?? 'Неизвестно';
        resultDuration.textContent = formatDuration(match.durationSeconds);
        resultWinner.textContent = match.winnerTeamName ?? 'Не определен';
        resultBox.style.display = 'block';

        setTimeout(() => {
            resultBox.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }, 100);
    }

    function hideResult() {
        resultBox.style.display = 'none';
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearStatus();
        hideResult();

        const matchId = input.value.trim();
        if (!matchId) {
            showStatus('⚠️ Введите ID матча.', 'error');
            input.focus();
            return;
        }

        if (!/^\d+$/.test(matchId)) {
            showStatus('❌ ID матча должен содержать только цифры.', 'error');
            input.focus();
            return;
        }

        submitButton.disabled = true;
        const originalButtonHtml = submitButton.innerHTML;
        submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Ищем матч...';

        try {
            const response = await fetch(`/api/integration/dota/match/${encodeURIComponent(matchId)}`, {
                headers: { Accept: 'application/json' }
            });

            const contentType = response.headers.get('content-type') || '';
            const payload = contentType.includes('application/json')
                ? await response.json()
                : { message: await response.text() };

            if (!response.ok) {
                showStatus(payload.message || 'Не удалось получить данные матча.', 'error');
                return;
            }

            if (payload.status && payload.status !== 'success') {
                showStatus(payload.message || 'Матч не найден.', 'error');
                return;
            }

            const match = payload.match ?? payload;
            renderResult(match);
            showStatus('Матч найден', 'success');
        } catch (error) {
            showStatus('❌ Ошибка сети или сервера при запросе к локальному API.', 'error');
            console.error('Error:', error);
        } finally {
            submitButton.disabled = false;
            submitButton.innerHTML = originalButtonHtml;
        }
    });

    input.addEventListener('input', () => {
        if (input.value && !/^\d+$/.test(input.value)) {
            input.classList.add('is-invalid');
        } else {
            input.classList.remove('is-invalid');
        }
    });
});

