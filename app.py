from flask import Flask, request, jsonify, render_template
from database import init_db, get_db_connection
from chatbot_ml import ChatbotML
from safety_monitor import SafetyMonitor
from feedback_analyzer import FeedbackAnalyzer
import sqlite3


app = Flask(__name__)

init_db()
chatbot = ChatbotML()
safety_monitor = SafetyMonitor()
feedback_analyzer = FeedbackAnalyzer()

chatbot.load_model()
safety_monitor.load_model()
feedback_analyzer.load_model()


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/api/chat', methods=['POST'])
def chat():
    user_message = request.json.get('message')
    if not user_message:
        return jsonify({'error': 'Message is required'}), 400

    conn = get_db_connection()
    cursor = conn.cursor()

    safety_severity, safety_reason = safety_monitor.check_safety(user_message)

    if safety_severity != "NORMAL":
        bot_response = (
            "TƏHLÜKƏSİZLİK BLOKU! Bu əməliyyatı gömrük. Zəhmət bank müştəri xidmətləri ilə əlaqə saxlayın: +9865"
        )

        cursor.execute(
            "INSERT INTO safety_incidents (user_message, severity) VALUES (?, ?)",
            (user_message, safety_severity),
        )
        conn.commit()

        cursor.execute(
            "INSERT INTO chat_messages (user_message, bot_response) VALUES (?, ?)",
            (user_message, bot_response),
        )
        conn.commit()

        conn.close()
        return jsonify({'response': bot_response, 'severity': safety_severity})

    bot_response = chatbot.get_response(user_message)

    cursor.execute(
        "INSERT INTO chat_messages (user_message, bot_response) VALUES (?, ?)",
        (user_message, bot_response),
    )
    conn.commit()
    conn.close()

    return jsonify({'response': bot_response, 'severity': 'NORMAL'})


@app.route('/api/feedback', methods=['POST'])
def submit_feedback():
    feedback_message = request.json.get('message')
    if not feedback_message:
        return jsonify({'error': 'Feedback message is required'}), 400

    severity, priority_score = feedback_analyzer.analyze_feedback(feedback_message)

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute(
        "INSERT INTO feedback (message, severity, priority_score) VALUES (?, ?, ?)",
        (feedback_message, severity, priority_score),
    )
    conn.commit()
    conn.close()

    return jsonify(
        {
            'message': 'Feedback successfully submitted.',
            'severity': severity,
            'priority_score': priority_score,
        }
    )


@app.route('/api/admin/dashboard', methods=['GET'])
def admin_dashboard():
    conn = get_db_connection()
    cursor = conn.cursor()

    chat_messages = cursor.execute(
        "SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 10"
    ).fetchall()

    safety_incidents = cursor.execute(
        "SELECT * FROM safety_incidents ORDER BY timestamp DESC LIMIT 10"
    ).fetchall()

    all_feedback = cursor.execute(
        "SELECT * FROM feedback ORDER BY timestamp DESC LIMIT 10"
    ).fetchall()

    conn.close()

    return jsonify(
        {
            'chat_messages': [dict(row) for row in chat_messages],
            'safety_incidents': [dict(row) for row in safety_incidents],
            'all_feedback': [dict(row) for row in all_feedback],
        }
    )


@app.route('/api/feedback/priority', methods=['GET'])
def get_priority_feedback():
    conn = get_db_connection()
    cursor = conn.cursor()

    priority_feedback = cursor.execute(
        "SELECT * FROM feedback WHERE severity IN ('CRITICAL', 'HIGH') ORDER BY priority_score DESC, timestamp DESC"
    ).fetchall()

    conn.close()

    return jsonify({'priority_feedback': [dict(row) for row in priority_feedback]})


if __name__ == '__main__':
    app.run(debug=True)

