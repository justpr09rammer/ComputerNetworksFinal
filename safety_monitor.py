import re
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline
import joblib


class SafetyMonitor:
    def __init__(self):
        self.model = None
        self.vectorizer = None
        self.keywords = {
            "CRITICAL": [
                "kartımdan pul çəkiblər",
                "kartım oğurlanıb",
                "hesabımdan pul itirdim",
                "kartımda qeyri-adi əməliyyat",
                "şübhəli köçürmə",
                "fırıldaq",
                "scam",
                "aldatma",
                "investisiya təklifi",
            ],
            "HIGH": [
                "kredit problemim var",
                "kart bloklanıb",
                "şəxsi məlumat itkisi",
                "sistem işləmir",
                "xəta baş verdi",
            ],
        }
        self.train_model()

    def train_model(self):
        corpus = []
        labels = []
        for severity, examples in self.keywords.items():
            for example in examples:
                corpus.append(example)
                labels.append(severity)

        normal_examples = [
            "salam",
            "necəsən",
            "kredit almaq istəyirəm",
            "vergi ödəmək lazımdır",
            "komissiyalar haqqında məlumat",
            "yaxşı xidmət",
            "suallarım var",
        ]
        for example in normal_examples:
            corpus.append(example)
            labels.append("NORMAL")

        self.model = make_pipeline(TfidfVectorizer(), LogisticRegression(solver='liblinear'))
        self.model.fit(corpus, labels)
        self.vectorizer = self.model.named_steps['tfidfvectorizer']

        joblib.dump(self.model, 'safety_model.pkl')
        print("Safety Monitor ML model trained and saved.")

    def load_model(self):
        try:
            self.model = joblib.load('safety_model.pkl')
            self.vectorizer = self.model.named_steps['tfidfvectorizer']
            print("Safety Monitor ML model loaded.")
        except FileNotFoundError:
            print("Safety Monitor ML model not found. Training a new one.")
            self.train_model()

    def check_safety(self, message):
        if not self.model:
            self.load_model()
            if not self.model:
                return "NORMAL", "Təhlükəsizlik yoxlaması aparıla bilmədi."

        message_lower = message.lower()
        for severity, keywords in self.keywords.items():
            for keyword in keywords:
                if keyword in message_lower:
                    return severity, f"Dərhal aşkarlanan '{severity}' təhlükə: '{keyword}'."

        prediction = self.model.predict([message_lower])[0]
        if prediction != "NORMAL":
            return prediction, f"ML tərəfindən '{prediction}' təhlükəsi aşkarlanıb."

        return "NORMAL", "Mesajda təhlükə aşkarlanmadı."


if __name__ == '__main__':
    monitor = SafetyMonitor()
    critical_msg = "Kartımdan pul çəkiblər!"
    severity, reason = monitor.check_safety(critical_msg)
    print(f"Message: '{critical_msg}' -> Severity: {severity}, Reason: {reason}")
    high_msg = "Sistem işləmir, kartım bloklanıb."
    severity, reason = monitor.check_safety(high_msg)
    print(f"Message: '{high_msg}' -> Severity: {severity}, Reason: {reason}")
    normal_msg = "Kredit almaq üçün nə etməliyəm?"
    severity, reason = monitor.check_safety(normal_msg)
    print(f"Message: '{normal_msg}' -> Severity: {severity}, Reason: {reason}")
    scam_msg = "Mənə investisiya təklifi gəlib, yoxlaya bilərsinizmi?"
    severity, reason = monitor.check_safety(scam_msg)
    print(f"Message: '{scam_msg}' -> Severity: {severity}, Reason: {reason}")

