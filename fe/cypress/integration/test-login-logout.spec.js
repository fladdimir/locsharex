/// <reference types="cypress" />

const testLogins = ["login-sherlock", "login-watson", "login-lestrade"];

context("test-login-logout", () => {
  beforeEach(() => {
    cy.intercept("GET", "**/login-info").as("login-info");
    cy.visit("http://localhost:8081");
    cy.wait("@login-info").its("response.statusCode").should("eq", 200);
    testLogins.forEach((s) => {
      cy.get(`button[id=${s}]`).as(s).should("be.enabled");
    });
  });

  it("should login and logout as first test user", () => {
    cy.get(`@${testLogins[0]}`).click();
  });

  it("should login and logout as second test user", () => {
    cy.get(`@${testLogins[1]}`).click();
  });

  it("should login and logout as third test user", () => {
    cy.get(`@${testLogins[2]}`).click();
  });

  afterEach(() => {
    cy.intercept("GET", "**/locations").as("locations");
    cy.wait("@login-info").its("response.statusCode").should("eq", 200);
    cy.wait("@locations").its("response.statusCode").should("eq", 200);

    cy.get("button[id=settingsMenuButton]").click();
    cy.get("button[id=logoutButton]").click();
    testLogins.forEach((s) => {
      cy.get(`button[id=${s}]`).as(s).should("be.enabled");
    });
  });
});
