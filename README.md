# Table of Contents
- [Authors](#authors)
- [App Mockup](#mockup)
- [Video Demo](#demo)
- [Project Wiki](#wiki)

## Authors <a name="authors"></a>
- Pushdeep Gangrade
- Katy Mitchell
- Valerie Ray
- Rockford Stoller

## App Mockup <a name="mockup"></a>
- Mockup: https://xd.adobe.com/view/5d826fe9-1fa4-4b11-bd5e-ce6f1dad719e-08d1

## Video Demo <a name="demo"></a>
- App Demo: https://www.youtube.com/watch?v=PZN-J7Q2kzw&feature=youtu.be

## Project Wiki <a name="wiki"></a>

### App Features
- This project extends Profile-Android to include mobile payments using the BrainTree SDK (https://www.braintreepayments.com/).
- User management, including login/logout, signup, view and edit profile.
- The user is provided with a list of products to purchase. They are able to view a product and add 
  it to their shopping cart.
- The shopping cart:
  - The user is able to view and delete the contents of the shopping cart.
  - If the user adds the same product to the cart then the quantity of that item is updated.
  - The user is able to clear all the shopping cart.
  - The user is able to checkout, which initiates the payment process.
- Payment Processing:
  - The DropIn UI is displayed to start the payment process, giving users the option of PayPal or credit 
    card. The user's previously used payment methods are also visible.
  - The user can complete the payment process using a new credit card or a previously used credit card.
- Order History:
  - The app should enable the user to view their previous orders and the details of each order.
  
  ### API Implementation and Design
- When a user is created, on the server, the BrainTree API should be contacted to create a user customerId on BrainTree, which should then be stored on your server to be used when the user attempts to make future purchases.
- The products information should be stored on the server, and should be retrievable through an API.
- The DropIn UI is displayed to start the payment process with the use of your server to generate the clientToken.
- The customerId is provided to BrainTree to enable BrainTree to display the user's previously used payment methods.
- The completed transactions are stored on the server.
- The app handles cases of invalid credit cards, using the cards provided at 
  https://developers.braintreepayments.com/guides/credit-cards/testing-go-live/php for testing

  <strong>Sign Up:</strong>
  <br />
  <img src="https://github.com/pushpdeep-gangrade/Profile-Android/blob/main/screenshots/SignupDocumentationAPI.png" width=800>
  <br />
  <strong>Login:</strong>
  <br />
  <img src="https://github.com/pushpdeep-gangrade/Profile-Android/blob/main/screenshots/LoginDocumentationAPI.png" width=800>
  <br />
  Profile:
  <br />
  ```
  Get Other User Profile
  GET METHOD
  http://104.248.113.55:8088/v1/user/profile/other/:email

  Request:
    Header:
      authorizationkey: user's generated key

  Response:
    Body:
     {
      "emailId": "jsmith@email.com",
      "age": "27",
      "fname": "John",
      "lname": "Smith",
      "address": "123 JS Street"
      }
  ```
  ```
  Get Current User Profile
  GET METHOD
  http://104.248.113.55:8088/v1/user/profile/me

  Request:
    Header:
      authorizationkey: user's generated key

  Response:
    Body:
     {
      "emailId": "jsmith@email.com",
      "age": "27",
      "fname": "John",
      "lname": "Smith",
      "address": "123 JS Street"
      }
  ```
  ```
  Get Current User Profile
  GET METHOD
  http://104.248.113.55:8088/v1/user/profile/me

  Request:
    Header:
      authorizationkey: user's generated key

  Response:
    Body:
     {
      "emailId": "jsmith@email.com",
      "age": "27",
      "fname": "John",
      "lname": "Smith",
      "address": "123 JS Street"
      }
  ```
  
  ### Database Schema
