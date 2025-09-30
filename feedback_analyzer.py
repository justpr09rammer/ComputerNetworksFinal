from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import SVC
from sklearn.pipeline import make_pipeline
import joblib


class FeedbackAnalyzer:
    def __init__(self):
        self.model = None
        self.vectorizer = None
        self.feedback_examples = {
            "CRITICAL": [
                "Çox pis xidmət, bu necə ola bilər?!",
                "Təcili kömək lazımdır, bankda fırıldaq gedir!",
                "Hesabımda qeyri-adi əməliyyatlar var, sisteminiz işləmir!",
            ],
            "HIGH": [
                "Sistem çox yavaş işləyir",
                "Mobil tətbiqdə xəta var",
                "Dəstək xidməti gec cavab verir",
                "Kartımla bağlı problemim var, həll olunmur",
                "Məlumatlarım səhv göstərilir",
            ],
            "MEDIUM": [
                "Bir az yavaş işləyir",
                "Dəstək xidmətində gözləmə vaxtı uzun idi",
                "Tətbiqin interfeysi daha yaxşı ola bilər",
                "Yeni funksiyalar əlavə etmək lazımdır",
            ],
            "LOW": [
                "Çox yaxşı işləyir, təşəkkürlər!",
                "Xidmətinizdən razıyam",
                "Hər şey qaydasındadır",
                "Mükəmməl təcrübə",
                "Əla xidmət!",
            ],
        }
        self.severity_map = {"CRITICAL": 1.0, "HIGH": 0.8, "MEDIUM": 0.5, "LOW": 0.2}
        self.train_model()

    def train_model(self):
        corpus = []
        labels = []
        for severity, examples in self.feedback_examples.items():
            for example in examples:
                corpus.append(example)
                labels.append(severity)

        self.model = make_pipeline(TfidfVectorizer(), SVC(kernel='linear', probability=True))
        self.model.fit(corpus, labels)
        self.vectorizer = self.model.named_steps['tfidfvectorizer']

        joblib.dump(self.model, 'feedback_model.pkl')
        print("Feedback Analyzer ML model trained and saved.")

    def load_model(self):
        try:
            self.model = joblib.load('feedback_model.pkl')
            self.vectorizer = self.model.named_steps['tfidfvectorizer']
            print("Feedback Analyzer ML model loaded.")
        except FileNotFoundError:
            print("Feedback Analyzer ML model not found. Training a new one.")
            self.train_model()

    def analyze_feedback(self, feedback_message):
        if not self.model:
            self.load_model()
            if not self.model:
                return "UNKNOWN", 0.0

        predicted_severity = self.model.predict([feedback_message.lower()])[0]
        priority_score = self.severity_map.get(predicted_severity, 0.0)
        return predicted_severity, priority_score


if __name__ == '__main__':
    analyzer = FeedbackAnalyzer()
    feedback1 = "Sistem çox yavaş işləyir"
    severity1, score1 = analyzer.analyze_feedback(feedback1)
    print(f"Feedback: '{feedback1}' -> Severity: {severity1}, Score: {score1}")
    feedback2 = "Çox yaxşı işləyir, təşəkkürlər!"
    severity2, score2 = analyzer.analyze_feedback(feedback2)
    print(f"Feedback: '{feedback2}' -> Severity: {severity2}, Score: {score2}")
    feedback3 = "Kartımdan pul çəkiblər!"
    severity3, score3 = analyzer.analyze_feedback(feedback3)
    print(f"Feedback: '{feedback3}' -> Severity: {severity3}, Score: {score3}")
    feedback4 = "Dəstək xidməti gözləmə müddəti çox uzundur."
    severity4, score4 = analyzer.analyze_feedback(feedback4)
    print(f"Feedback: '{feedback4}' -> Severity: {severity4}, Score: {score4}")

