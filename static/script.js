document.addEventListener('DOMContentLoaded', () => {
    const chatBox = document.getElementById('chat-box');
    const userInput = document.getElementById('user-input');
    const sendButton = document.getElementById('send-button');
    const feedbackInput = document.getElementById('feedback-input');
    const feedbackButton = document.getElementById('feedback-button');
    const feedbackStatus = document.getElementById('feedback-status');

    function addMessage(sender, message, severity = 'NORMAL') {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', sender);
        if (sender === 'bot') {
            if (severity === 'CRITICAL') {
                messageElement.classList.add('critical');
            } else if (severity === 'HIGH') {
                messageElement.classList.add('high');
            }
        }
        messageElement.textContent = message;
        chatBox.appendChild(messageElement);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    sendButton.addEventListener('click', async () => {
        const message = userInput.value.trim();
        if (message) {
            addMessage('user', message);
            userInput.value = '';
            try {
                const response = await fetch('/api/chat', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ message })
                });
                const data = await response.json();
                addMessage('bot', data.response, data.severity);
            } catch (error) {
                console.error('Error sending message:', error);
                addMessage('bot', 'Bağışlayın, hazırda cavab verə bilmirəm.');
            }
        }
    });

    userInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            sendButton.click();
        }
    });

    feedbackButton.addEventListener('click', async () => {
        const feedbackMessage = feedbackInput.value.trim();
        if (feedbackMessage) {
            try {
                const response = await fetch('/api/feedback', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ message: feedbackMessage })
                });
                const data = await response.json();
                feedbackStatus.textContent = `Geribildirim göndərildi! Severity: ${data.severity}, Priority Score: ${data.priority_score.toFixed(2)}`;
                feedbackInput.value = '';
            } catch (error) {
                console.error('Error submitting feedback:', error);
                feedbackStatus.textContent = 'Geribildirim göndərilərkən xəta baş verdi.';
            }
        } else {
            feedbackStatus.textContent = 'Zəhmət olmasa geribildirim mesajınızı daxil edin.';
        }
    });

    addMessage('bot', 'Salam! Azərbaycan Beynəlxalq Bankının KOB Chatbotuna xoş gəlmisiniz. Necə kömək edə bilərəm?', 'NORMAL');
});

