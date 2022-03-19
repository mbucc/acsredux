// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//

const acsPwd='abCd3fg!';
const acsEmail1='t@t.com'
const acsEmail2='2@t.com'

// -- This is a parent command --
Cypress.Commands.add('createMember1', () => {
    cy.visit('/')
    cy.get(':nth-child(3) > a')
        .should('have.text', 'Become a member')
        .click()
    cy.get('#email').type(acsEmail1)
    cy.get('#firstName').type(`Joe`)
    cy.get('#lastName').type(`Smith`)
    cy.get('#zip').type(`01002`)
    cy.get('#pwd1').type(acsPwd)
    cy.get('#pwd2').type(`${acsPwd}{enter}`)
})

Cypress.Commands.add('createMember2', () => {
    cy.visit('/')
    cy.get(':nth-child(3) > a')
        .should('have.text', 'Become a member')
        .click()
    cy.get('#email').type(acsEmail2)
    cy.get('#firstName').type(`Patti`)
    cy.get('#lastName').type(`Smith`)
    cy.get('#zip').type(`02134`)
    cy.get('#pwd1').type(acsPwd)
    cy.get('#pwd2').type(`${acsPwd}{enter}`)
})

Cypress.Commands.add('loginMember1', () => {
    cy.visit('/members/login')
    cy.get('#email').type(acsEmail1)
    cy.get('#pwd').type(`${acsPwd}{enter}`)
})

Cypress.Commands.add('loginMember2', () => {
    cy.visit('/members/login')
    cy.get('#email').type(acsEmail2)
    cy.get('#pwd').type(`${acsPwd}{enter}`)
})


// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
