const scanner = require("sonarqube-scanner");

scanner(
  {
    serverUrl: "http://localhost:9000",
    options: {
      "sonar.login": "admin",
      "sonar.password": "a",
      "sonar.projectName": "Location Sharing FE",
      "sonar.sources": "src",
    },
  },
  () => process.exit()
);
