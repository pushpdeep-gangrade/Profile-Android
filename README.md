# Table of Contents
- [Authors](#authors)
- [App Mockup](#mockup)
- [Video Demo](#demo)
- [API Documentation](#documentation)

## Authors <a name="authors"></a>
- Pushdeep Gangrade
- Katy Mitchell
- Valerie Ray
- Rockford Stoller

## App Mockup <a name="mockup"></a>
### Update!
- Mockup: https://xd.adobe.com/view/3d3d8977-2968-41a0-8daa-7e228678c95b-686e/

## Video Demo <a name="demo"></a>
### Update!
- App Demo: https://youtu.be/YlHDhZJQv78
- Postman Demo: https://youtu.be/Frp-nyx03F8

## API Documentation <a name="documentation"></a>
### Update!
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
<img src="https://github.com/pushpdeep-gangrade/Profile-Android/blob/main/screenshots/ProfileDocumentationGETAPI.png" width=800>
<br />
<img src="https://github.com/pushpdeep-gangrade/Profile-Android/blob/main/screenshots/ProfileDocumentationPOSTAPI.png" width=800>

## P5 Requirements (Due Wed. 10/14)
### Cross off when done

- In this assignment you will develop a mobile application and any supporting server apis to provide mobile payments. Your app should provide the following features:
  - The app should use the BrainTree SDK (https://www.braintreepayments.com/ , you will require to use the server SDK and the client SDK for DropIn UI.
  - The app should provide user management features including login, signup and logout.
  - When a user is created, on the server, the BrainTree API should be contacted to create a user customerId on BrainTree, which should then be stored on your server to be used when the user attempts to make future purchases.
  - The app provides the user with a list of products to purchase, the user is able to view a product and add it to their shopping cart.  You are provided with a list of products, pricing and discounts in the provided support file [ZIP].
  - The products information should be stored on the server, and should be retrievable through an API.
- The shopping cart:
  - The user is able to view the contents of the shopping cart, and should be able to delete items from the cart.
  - If the user adds the same product to the cart then the shopping cart should be updated to reflect the updated quantity of the same product selected.
  - The user is able to clear all the shopping cart.
  - The user is able to checkout, which should initiate the payment process.
- Payment Processing:
  - The DropIn UI should be displayed to start the payment process with the use of your server to generate the clientToken.
  - The customerId should be provided to BrainTree to enable BrainTree to display the user's previously used payment methods.
  - The user should be able to complete the payment process using a new credit card or a previously used credit card.
  - The completed transactions should be stored on your server.
- The app should also handle cases of invalid credit cards, use the cards provided at https://developers.braintreepayments.com/guides/credit-cards/testing-go-live/php for testing.
- Order History:
  - The app should enable the user to view their previous orders and the details of each order.
- Submission should include:
  - Create a Github or Bitbucket repo for the assignment.
  - Push your code to the created repo. Should contain both the mobile and web code. 
  - On the same repo create a wiki page describing your api design and implementation. The wiki page should describe the API routes, DB Schema and all the assumptions required to provide authentication. In addition describe any data that is stored on the device or on the server.
  - Include the Postman file in the repo.
  - The API should be demonstrated using Postman, you should create an api component in Postman for each of your created APIs.
  - Demo your App and record an app screencast showing the different app features. Your video should be posted on Github and included with your submission.