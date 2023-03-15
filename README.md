# dailybibleverse
 PIP 每日聖經金句電郵推送 (PIP Daily Bible Verse): <br /><br />
 一個十分簡單的每日接收聖經金句電郵的 App. 學習之用， 會放在 Google App Engine 內， 當中用了 Spring Boot、 Google ReCAPCHA v2、 JSON Web Token， Google Firestore Database 等技術。
# 前期設置:
- 請先填入所有參數在  "application.properties" 內， 如你的 Gmail 的電郵地址、 App password、 Google ReCAPCHA v2 的 Site Key、 Secret Key 等等．．． <br />
- 張你的 Google Firebase Service Account Private Key 檔案內容複製去 "serviceAccount.json"， 或可用你的檔案覆蓋它。<br />
# 簡介:
- 在第一欄可輸入你的電郵地址， 按 "確定" 登記。<br />
- 然後你所登記的電子郵件箱內會收到一封確認電郵， 內裏有一條 "Verify" 的鏈結， 按下它 或 複製這條鏈結， 在瀏覽器內開啟。<br />
- 如成功確定， 你的電子郵件箱內會在每日香港時間早上六時收到聖經金句郵件。<br />
- 如想取消登記， 可在第二欄內輸入你的電郵地址， 按 "確定" 以取消登記。<br />
# 測試和執行:
- mvnw -DskipTests spring-boot:run<br />
- http://localhost:8080<br /><br />
- Deploy to Google App Engine: mvnw -DskipTests package appengine:deploy<br />
# Online demo:
- <a href="https://pip-dbv.uc.r.appspot.com/">PIP Daily Bible Verse</a><br />
(My Google Firesbase Account is "Spark" plan, so if the transactions exceed the limit, there may be some problems encountered when accessing the demo; then need to try again later)

