describe('ACS Redux tests', () => {

  it('can create account and then logout', () => {
    cy.createMember1();
    cy.visit('/members/logout')
    cy.visit('/')
    cy.contains('Become a member')
    cy.getCookie('session_id').should('not.exist')
  })

  it('can login', () => {
    cy.loginMember1();
    cy.getCookie('session_id').should('exist')
  })

  it('after logging in, a user sees create diary link', () => {
    cy.loginMember1();
    cy.contains('Create a new photo diary.')
  })

  it('logged in user sees dashboard menu link', () => {
    cy.loginMember1();
    cy.contains('dashboard')
  })

  it('can verify email', () => {
    cy.loginMember1();
    cy.visit('/members/2?token=token2')
    cy.getCookie('session_id').should('exist')
  })

  it('can create a photo diary', () => {
    cy.loginMember1();
    cy.get('[href="/photo-diary/create"]').click()
    cy.get('#year').type('2022')
    cy.get('#name').type(`back yard{enter}`)
  })

  it('can add a photo', () => {
    cy.loginMember1();
      cy.get('li > a').click()
      cy.get(':nth-child(4) > a > button').click()
      cy.get('#picker').selectFile('cypress/fixtures/10138-80-prospect-business-hours-medium.jpeg')
      cy.get(':nth-child(2) > input').click()

      // Uploading photo redirects back to diary.
      cy.location('pathname').should('eq', '/photo-diary/1')

      // And the image should actually load.
      // "naturalWidth" and "naturalHeight" are set when the image loads
      cy.get('[alt="10138-80-prospect-business-hours-medium"]')
      .should('be.visible')
      .and(($img) => {
        expect($img[0].naturalWidth).to.be.greaterThan(0)
      })
    })

  it('can create a second member', () => {
    cy.createMember2();
    cy.getCookie('session_id').should('exist')
  })

  it('member 2 does not see a create diary link on member 1 dashboard', () => {
    cy.loginMember2()
    cy.visit('/members/2')
    cy.contains('2022: back yard')
    cy.contains('Create a photo diary').should('not.exist')
  })

  it('member 2 does not see an add image button on member 1 photo diary', () => {
    cy.loginMember2()
    cy.visit('/photo-diary/1')
    cy.contains('+ img').should('not.exist')
  })


})
