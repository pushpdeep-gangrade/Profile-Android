# Authors
- Pushdeep Gangrade
- Katy Mitchell
- Valerie Ray
- Rockford Stoller

# Table of Contents
- [App Mockup](#mockup)
- [Video Demo](#demo)
- tbd

# Chatroom App Mockup <a name="mockup"></a>

# Chatroom Demo <a name="demo"></a>

# Project requirements (cross out when fulfilled)
### Goal: Create a simple authentication API for mobile application
1. Not allowed to use frameworks such as Firebase, or Parse.com. 
2. Use NodeJS and Express framework to create this app.
3. Use an online provisioning provider, such as Heroku, Amazon AWS, or Microsoft Azure, or others.
4. API should provide:
   - sign in, login, and logout features. (Use JWT tokens!!)
   - a mechanism to allow the user to access protected resources after a successful login. For example, the protected api could return the user's profile information (name, age, weight, address). This api should only work for logged-in users.
   - an error reporting mechanism. All data returned by the API should be in JSON.
5. The API can be implemented using NodeJS and Express Framework.
6. The API should be deployed on a remote server, which means local host is not acceptable for your submission. The user data profile information should be stored on a remote database, you can use MongoDB or any database of your choice.
7. Mobile App: 
   - API should be connected to the mobile application, and should also demonstrate the protected API.
   - Sign Up (Registration), Login, Profile Screen.
   - The profile screen should allow the user to view and edit their profile information.
8. Submission should include:
   - ~~Create a Github or Bitbucket repo for the assignment~~
   - Push your code to the created repo. Should contain both the mobile and web code. 
   - On the same repo create a wiki page describing your api design and implementation. The wiki page should describe the API routes, DB Schema and all the assumptions required to provide authentication. In addition describe any data that is stored on the device or on the server.
   - Include the Postman file in the repo.
   - The API should be demonstrated using Postman, you should create an api component in Postman for each of your created APIs.
   - Demo your API using a mobile app that uses your implemented api.