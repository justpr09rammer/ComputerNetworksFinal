from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import SVC
from sklearn.pipeline import make_pipeline
import joblib


class ChatbotML:
    def __init__(self):
        self.model = None
        self.vectorizer = None
        self.intents = {
            "kredit": [
                "kredit almaq",
                "kredit şərtləri",
                "pul götürmək",
                "faiz dərəcələri",
            ],
            "vergi": ["vergi ödəmək", "vergi məlumatları", "vergi borcu"],
            "komissiya": ["komissiya haqqı", "ödənişlər", "POS komissiyası"],
            "biznes_məsləhət": [
                "biznes açmaq",
                "şirkət qeydiyyatı",
                "işə başlamaq",
            ],
            "salamlama": ["salam", "sabahınız xeyir", "axşamınız xeyir", "necəsən"],
            "sağollaşma": ["sağol", "hələlik", "görüşənədək"],
        }
        self.responses = {
            "kredit": "Kredit almaq üçün minimum 6 ay fəaliyyətiniz və minimum 1000 AZN aylıq gəliriniz olmalıdır.",
            "vergi": "Vergi ödənişlərini onlayn, bank vasitəsilə və ya ASAN xidmətdə edə bilərsiniz.",
            "komissiya": "POS əməliyyatları üçün 1.5%, onlayn əməliyyatlar üçün 1% komissiya tutulur.",
            "biznes_məsləhət": "Biznes məsləhətləri üçün filiallarımızla əlaqə saxlaya və ya veb saytımızdakı KOB bölməsinə baxa bilərsiniz.",
            "salamlama": "Salam! Necə kömək edə bilərəm?",
            "sağollaşma": "Uğurlar! Yenidən köməyə ehtiyacınız olarsa, müraciət edə bilərsiniz.",
        }
        self.train_model()

    def train_model(self):
        corpus = []
        labels = []
        for intent, examples in self.intents.items():
            for example in examples:
                corpus.append(example)
                labels.append(intent)

        self.model = make_pipeline(TfidfVectorizer(), SVC(kernel='linear', probability=True))
        self.model.fit(corpus, labels)
        self.vectorizer = self.model.named_steps['tfidfvectorizer']

        joblib.dump(self.model, 'chatbot_model.pkl')
        print("Chatbot ML model trained and saved.")

    def load_model(self):
        try:
            self.model = joblib.load('chatbot_model.pkl')
            self.vectorizer = self.model.named_steps['tfidfvectorizer']
            print("Chatbot ML model loaded.")
        except FileNotFoundError:
            print("Chatbot ML model not found. Training a new one.")
            self.train_model()

    def get_response(self, user_message):
        if not self.model:
            self.load_model()
            if not self.model:
                return "Bağışlayın, hazırda cavab verə bilmirəm."

        intent_probs = self.model.predict_proba([user_message])[0]
        max_prob = max(intent_probs)
        predicted_intent = self.model.predict([user_message])[0]

        if max_prob > 0.6:
            return self.responses.get(predicted_intent, "Üzr istəyirəm, sorğunuzu başa düşmədim.")
        else:
            return "Üzr istəyirəm, sorğunuzu başa düşmədim. Daha dəqiq ifadə edə bilərsinizmi?"


if __name__ == '__main__':
    bot = ChatbotML()
    print(f"User: Kredit almaq üçün nə lazımdır?")
    print(f"Bot: {bot.get_response('Kredit almaq üçün nə lazımdır?')}\n")
    print(f"User: Vergi ödəmək istəyirəm.")
    print(f"Bot: {bot.get_response('Vergi ödəmək istəyirəm.')}\n")
    print(f"User: Şirkət açmaq istəyirəm.")
    print(f"Bot: {bot.get_response('Şirkət açmaq istəyirəm.')}\n")
    print(f"User: Salam!")
    print(f"Bot: {bot.get_response('Salam!')}\n")
    print(f"User: Bilmirəm.")
    print(f"Bot: {bot.get_response('Bilmirəm.')}\n")

