describe('Edit Text Control Tests', () => {

  it('can create a text note', () => {
    cy.createMember1();
    cy.get('[href="/photo-diary/create"]').click()
    cy.get('#year').type('2022')
    cy.get('#name').type(`back yard{enter}`)
    cy.location('pathname').should('eq', '/photo-diary/1')
    cy.get('#edit-1').click()
    cy.get('#textarea-1').type('test text')
    cy.get('#save-1').click()

  })


})
