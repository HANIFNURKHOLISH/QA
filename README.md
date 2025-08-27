# QA
📚 Book API Test Automation
Automation testing project for Book API using:
- RestAssured (API testing)
- JUnit5 (test runner)
- Allure Report (reporting)
- AssertJ SoftAssertions (flexible verification)
🚀 Setup & Dependencies
Tambahkan ke pom.xml:

<dependencies>
  <dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.24.0</version>
  </dependency>
  <dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.25.3</version>
    <scope>test</scope>
  </dependency>
</dependencies>
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.2.5</version>
    </plugin>
    <plugin>
      <groupId>io.qameta.allure</groupId>
      <artifactId>allure-maven</artifactId>
      <version>2.12.0</version>
    </plugin>
  </plugins>
</build>

▶️ Run Tests
Run semua test:
mvn clean test
📊 Allure Report
Generate & buka report:
allure serve allure-results
Atau generate static HTML:
allure generate allure-results -o allure-report --clean
Buka di browser: allure-report/index.html
🧪 Test Scenarios
Positive
- GET all books → 200
- POST new book → 201
- PUT update book → 200
- DELETE book → 200/204
Negative
- GET wrong endpoint → 404
- POST without title → 400
- POST with invalid status → 400
- PUT non-existing ID → 404
- PUT empty body → 400
- DELETE non-existing ID → 404
📌 Notes
- Menggunakan SoftAssertions (AssertJ) → semua verifikasi dijalankan dalam 1 test. 
- Allure melampirkan Request Payload, Response Status, Response Body untuk debugging.
