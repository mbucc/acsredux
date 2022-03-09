describe('ACS Redux tests', () => {
  beforeEach(() => {
    // Start up the app outside of Cypress.
  })

  it('can create account and then logout', () => {
    const pwd = 'abCd3fg!'

    cy.visit('/')

    cy.get(':nth-child(3) > a')
        .should('have.text', 'Become a member').click()

    cy.get('#email').type(`t@t.com`)
    cy.get('#firstName').type(`Joe`)
    cy.get('#lastName').type(`Smith`)
    cy.get('#zip').type(`01002`)
    cy.get('#pwd1').type(`${pwd}`)
    cy.get('#pwd2').type(`${pwd}{enter}`)

    cy.contains('Create a photo diary')
    cy.getCookie('session_id').should('exist')

    cy.visit('/members/logout')
    cy.visit('/')
    cy.contains('Become a member')
    cy.getCookie('session_id').should('not.exist')
  })

  it('can login', () => {
    cy.visit('/members/login')
    cy.get('#email').type(`t@t.com`)
    cy.get('#pwd').type(`abCd3fg!{enter}`)

    cy.contains('Create a photo diary')

    cy.getCookie('session_id').should('exist')

  })

  it('can verify email', () => {
    cy.visit('/members/login')
    cy.get('#email').type(`t@t.com`)
    cy.get('#pwd').type(`abCd3fg!{enter}`)
    cy.visit('/members/2?token=token2')
    cy.getCookie('session_id').should('exist')
  })

  it('can create a photo diary', () => {
    cy.visit('/members/login')
    cy.get('#email').type(`t@t.com`)
    cy.get('#pwd').type(`abCd3fg!{enter}`)
    cy.get('[href="/photo-diary/create"]').click()
    cy.get('#year').type('2022')
    cy.get('#name').type(`back yard{enter}`)
  })

  it('can add a photo', () => {
      cy.visit('/members/login')
      cy.get('#email').type(`t@t.com`)
      cy.get('#pwd').type(`abCd3fg!{enter}`)
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
    const pwd = 'abCd3fg!'
    cy.visit('/')
    cy.get(':nth-child(3) > a')
        .should('have.text', 'Become a member').click()
    cy.get('#email').type(`u@t.com`)
    cy.get('#firstName').type(`Patti`)
    cy.get('#lastName').type(`Smith`)
    cy.get('#zip').type(`02134`)
    cy.get('#pwd1').type(`${pwd}`)
    cy.get('#pwd2').type(`${pwd}{enter}`)
    cy.contains('Create a photo diary')
    cy.getCookie('session_id').should('exist')
  })

  it('member 2 does not see a create diary link on member 1 dashboard', () => {
    const pwd = 'abCd3fg!'
    cy.visit('/members/login')
    cy.get('#email').type(`u@t.com`)
    cy.get('#pwd').type(`abCd3fg!{enter}`)
    cy.contains('Create a photo diary')
    cy.getCookie('session_id').should('exist')
    cy.visit('/members/2')
    cy.contains('2022: back yard')
    cy.contains('Create a photo diary').should('not.exist')
  })

  it('member 2 does not see an add image button on member 1 photo diary', () => {
    const pwd = 'abCd3fg!'
    cy.visit('/members/login')
    cy.get('#email').type(`u@t.com`)
    cy.get('#pwd').type(`abCd3fg!{enter}`)
    cy.visit('/photo-diary/1')
    cy.contains('+ img').should('not.exist')
  })


})
