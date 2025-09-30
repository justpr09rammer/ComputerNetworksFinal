ABB KOB Chatbot (MVP)

Quraşdırma və işə salma

1) Virtual mühit yaradın və aktivləşdirin

Linux/macOS:

python3 -m venv venv
source venv/bin/activate

Windows (PowerShell):

python -m venv venv
venv\\Scripts\\Activate.ps1

2) Asılılıqları quraşdırın

pip install -r requirements.txt

3) Verilənlər bazasını ilkinləşdirin

python database.py

4) Tətbiqi işə salın

python app.py

Tətbiq standart olaraq http://127.0.0.1:5000/ ünvanında açılacaq.

Fayl strukturu

- app.py — Flask tətbiqi və API marşrutları
- database.py — SQLite cədvəllərinin yaradılması və əlaqə funksiyası
- chatbot_ml.py — Niyyət təsnifatı ilə sadə ML cavablayıcı
- safety_monitor.py — Təhlükəli məzmunun aşkarlanması (kritik/yüksək)
- feedback_analyzer.py — Geribildirimlərin prioritetləndirilməsi
- templates/index.html — UI
- static/style.css, static/script.js — stil və müştəri JS

Qeydlər

- Bu layihə MVP-dir. Real istehsalat üçün autentifikasiya, daha möhkəm səhv idarəetməsi, daha zəngin ML modelləri və miqyaslana bilən DB tövsiyə olunur.
