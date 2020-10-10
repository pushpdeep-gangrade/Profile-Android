const express = require('express');
const cors = require('cors');
const app = express();
var mysql = require('mysql');
const port = 8080
const jwt = require('jsonwebtoken');

//swagger for documentation
const swaggerJSDoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

const MongoClient = require('mongodb').MongoClient;
const url = "mongodb+srv://Pushp:pushp@a-mad-cluster.1u5jl.mongodb.net/API?retryWrites=true&w=majority";
//const client = new MongoClient(url ,{ useNewUrlParser: true, useUnifiedTopology: true });

app.use(cors());

var bodyParser = require('body-parser');
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

//Status encoded
const OK = 200;
const BAD_REQUEST = 400;
const UNAUTHORIZED = 401;
const NOT_FOUND = 404;
const INTERNAL_SERVER_ERROR = 500;

const swaggerOptions ={
  definition :{
    info :
      {
        "version" : "1.0.0",
    "title": "User API",
    "description": "User API documentation. This API can be used to design login, signup and viewing user profile.",
    "contact": {
      "name": "Pushpdeep Gangrade, Rockford Stroller, Valerie Ray, Katy Mitchel",
      "url": "https://github.com/pushpdeep-gangrade/Profile-Android",
    },
    "servers" : ["http://localhost:8080/"]
  }
},
    apis: ["api.js"]
}

const swaggerSpec = swaggerJSDoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));


app.use('/v1/user/profile',function(req,res,next){
  try {
    if(!req.headers.authorizationkey){
      res.status(UNAUTHORIZED).send("UNAUTHORIZED");
    }
    else{
      var decode = jwt.decode(req.headers.authorizationkey);
      jwt.verify(req.headers.authorizationkey, 'secret', function(err, decoded) {
        if(err){
          res.status(BAD_REQUEST).send(err.message);
        }
        else{
         if(decoded.emailId == decode.emailId){
           req.encode = decoded.emailId;
           next();
         }
         else
         console.log("fail");
      }
      });
    }
} catch(err) {
  res.send(err);
}
});

app.post('/v1/user/login',function(req,res){
  if (typeof req.body.email === "undefined" || typeof req.body.password === "undefined"){
    res.status(BAD_REQUEST).send("Bad request Check request Body");
  } else {
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {
      var myObj = {
        emailId: req.body.email,
        password: req.body.password
      };
      client.db('API').collection('User').findOne(myObj,function (err,result){
        if (err)
          res.status(INTERNAL_SERVER_ERROR).send(err);
        else if(result == null)
          res.status(OK).send("Invalid Credentials");
        else if(result !=null){
          var token = jwt.sign({ emailId: req.body.email,}, 'secret',{expiresIn : 60 * 60} );
          res.header("AuthorizationKey", token).status(OK).send(result);
        }
        return client.close();
      })
    });
  }
});

app.get('/v1/user/profile/:email',function(req,res){
  console.log("get" + req.encode);
  if(!req.params){
    res.status(BAD_REQUEST).send("Bad request Check parameters");
  }
  else {
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {
      client.db('API').collection('User').findOne({ _id :req.encode},function (err,result){
        if (err)
          console.log(err);
        else {
          res.status(OK).send(result);
        }
        return client.close();
      })
    });
  }
});

app.post('/v1/user/profile/:email',function(req,res){
  if (typeof req.body.password === "undefined" || typeof req.body.age === "undefined" ||
  typeof req.body.firstname === "undefined" || typeof req.body.lastname === "undefined" ||
  typeof req.body.address === "undefined"){
    res.status(BAD_REQUEST).send("Bad request Check parameters or Body");
  } else {
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {
      var myquery = { _id: req.encode };
      var newvalues = {
        $set: {
          password: req.body.password,
          age :req.body.age ,
          fname : req.body.firstname,
          lname: req.body.lastname,
          address: req.body.address
        }
      };
      client.db('API').collection('User').updateOne(myquery, newvalues,function (err,result){
        if (err)
          res.status(INTERNAL_SERVER_ERROR).send(err);
        else {
          res.status(OK).send("Record Updated");
        }
        return client.close();
      })
    });
  }
});


app.post('/v1/user/signup', (req, res) => {
  if(typeof req.body.email === "undefined" || typeof req.body.password === "undefined" || typeof req.body.age === "undefined" ||
  typeof req.body.firstname === "undefined" || typeof req.body.lastname === "undefined" || typeof req.body.address === "undefined"){
    res.status(BAD_REQUEST).send("Bad request Check parameters or Body");
  } else {
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {
      var myobj = {
        _id : req.body.email,
        emailId: req.body.email,
        password: req.body.password,
        age: req.body.age ,
        fname: req.body.firstname,
        lname: req.body.lastname,
        address: req.body.address 
      };
      client.db('API').collection('User').insertOne(myobj,function (err,result){
        if (err) {
          res.status(INTERNAL_SERVER_ERROR).send(err);
        } else if(result.insertedCount == 1){
          res.status(OK).send("Signed up Successfully");
        }
        return client.close();
      })
    });
  }
});

app.listen(port, (req, res) => {
  console.log("listening..." + port);
});


 /**
  * @swagger
  * definitions:
  *   login:
  *     properties:
  *       email:
  *         type: string
  *         description: User's email address.
  *       password:
  *         type: string
  *         description: User's passowrd.
  */

  /**
   * @swagger
   * definitions:
   *   signup:
   *     properties:
   *       email:
   *         type: string
   *         description: User's email address.
   *       password:
   *         type: string
   *         description: User's passowrd.
   *       age:
   *         type: integer
   *         description: User's age.
   *       firstname:
   *         type: string
   *         description: User's firstname.
   *       lastname:
   *         type: string
   *         description: User's lastname.
   *       address:
   *         type: string
   *         description: User's address.
   */


   /**
    * @swagger
    * definitions:
    *   updateProfile:
    *     properties:
    *       password:
    *         type: string
    *         description: User's email address.
    *       age:
    *         type: integer
    *         description: User's age.
    *       firstname:
    *         type: string
    *         description: User's firstname.
    *       lastname:
    *         type: string
    *         description: User's lastname.
    *       address:
    *         type: string
    *         description: User's address.
    */


    /**
    * @swagger
    * /v1/user/login:
    *   post:
    *     tags:
    *       - login API
    *     summary: login API
    *     description: login API, it returns authorizationkey on successful login.
    *     security:
    *       - bearerAuth: []
    *     consumes:
    *       - application/json
    *     produces:
    *       - application/json
    *     parameters:
    *       - name: body
    *         in: body
    *         required: true
    *         type: string
    *         description: User email id and password
    *         schema:
    *           $ref: '#/definitions/login'
    *     responses:
    *       200:
    *         description: A successful call returns response with authorizationkey in the header.
    *       400:
    *         description: Error code and error message returned in JSON.
    */

    /**
    * @swagger
    * /v1/user/signup:
    *   post:
    *     tags:
    *       - Signup API
    *     summary: login API
    *     description: login API, it returns authorizationkey on successful login.
    *     security:
    *       - bearerAuth: []
    *     consumes:
    *       - application/json
    *     produces:
    *       - application/json
    *     parameters:
    *       - name: body
    *         in: body
    *         required: true
    *         type: string
    *         description: User's sign up details
    *         schema:
    *           $ref: '#/definitions/signup'
    *     responses:
    *       200:
    *         description: A successful call returns response with authorizationkey in the header.
    *       400:
    *         description: Error code and error message returned in JSON.
    */




    /**
    * @swagger
    * /v1/user/profile/{email}:
    *   post:
    *     tags:
    *       - Profile API
    *     summary: update profile API
    *     description: Update profile API, it update the user's details.
    *     security:
    *       - bearerAuth: []
    *     consumes:
    *       - application/json
    *     produces:
    *       - application/json
    *     parameters:
    *       - name: authorizationkey
    *         in: header
    *         type: string
    *         description: Authorization key
    *       - name: email
    *         in: path
    *         required: true
    *         type: string
    *         description: User's emailId
    *       - name: user details
    *         in: body
    *         required: true
    *         type: string
    *         description: User's profile data
    *         schema:
    *           $ref: '#/definitions/updateProfile'
    *     responses:
    *       200:
    *         description: A successful call returns response with authorizationkey in the header.
    *       400:
    *         description: Error code and error message returned in JSON.
    */


    /**
    * @swagger
    * /v1/user/profile/1:
    *   get:
    *     tags:
    *       - Profile API
    *     summary: Get profile API
    *     description: Update profile API, it update the user's details.
    *     security:
    *       - bearerAuth: []
    *     consumes:
    *       - application/json
    *     produces:
    *       - application/json
    *     parameters:
    *       - name: authorizationkey
    *         in: header
    *         type: string
    *         description: Authorization key
    *     responses:
    *       200:
    *         description: A successful call returns response with authorizationkey in the header.
    *         schema:
    *           $$ref: '#/definitions/updateProfile'
    *       400:
    *         description: Error code and error message returned in JSON.
    */
