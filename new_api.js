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
const CONFLICT = 403;
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

//Swagger (Middleware)
const swaggerSpec = swaggerJSDoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

//Check JWT Token [on Profile Endpoints] (Middleware)
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

//Sign User Up With Passed Information (POST)
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
      client.db('API').collection('User').insertOne(myobj).then(thenResult => {
        //Populate empty cart for user on signup
        var myobj2 = {
          _id: req.body.email+"-cart",
          items: []
        };
        
        client.db('API').collection('Cart').insertOne(myobj2).then(thenResult2 => {
          //Populate empty order history for user on signup
          var myobj3 = {
            _id: req.body.email+"-orders",
            orders: []
          };
          
          client.db('API').collection('OrderHistory').insertOne(myobj3, function (err3,result3){
            if (err3)
              res.status(INTERNAL_SERVER_ERROR).send(err3);
            else if(result3.insertedCount == 1){
              res.status(OK).send("Signed up Successfully");
            }
            //return client.close();
          })
        }).catch(err2 => res.status(INTERNAL_SERVER_ERROR).send(err2))
      }).catch(err => {
        if (err.code == 11000) {
          res.status(CONFLICT).send("User with this Email Already Exists")
        } else {
          res.status(INTERNAL_SERVER_ERROR).send(err)
        }
      })
    });
  }
});

//Log User In (POST)
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

//Retrive Another User's Profile Information (GET)
app.get('/v1/user/profile/other/:email',function(req,res){
  if(!req.params){
    res.status(BAD_REQUEST).send("Bad request Check parameters");
  }
  else {
    console.log("get " + req.params.email);

    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {
      client.db('API').collection('User').findOne({ _id :req.params.email},function (err,result){
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

//Retrive Current User's Profile Information (GET)
app.get('/v1/user/profile/me',function(req, res){
  console.log("get me: " + req.encode);
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

//Update Profile (POST)
app.post('/v1/user/profile/me',function(req,res){
  console.log("post " + req.encode);
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

//Retieve Current User's Shopping Cart (GET)
app.get('/v1/user/profile/cart',function(req, res){
  console.log("get cart: " + req.encode+"-cart");
  if(!req.params){
    res.status(BAD_REQUEST).send("Bad request Check parameters");
  }
  else {
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {
      client.db('API').collection('Cart').findOne({ _id :req.encode+"-cart"},function (err,result){
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

//Update Current User's Shopping Cart (POST)
app.post('/v1/user/profile/cart',function(req,res){
  console.log("post cart: " + req.encode+"-cart");
  if (typeof req.body.name === "undefined" || typeof req.body.discount === "undefined" ||
  typeof req.body.photo === "undefined" || typeof req.body.price === "undefined" ||
  typeof req.body.region === "undefined" || typeof req.body.quantity === "undefined"){
    res.status(BAD_REQUEST).send("Bad request Check parameters or Body");
  } else {
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {    
      //Check if item is in cart already  
      client.db('API').collection('Cart').findOne({_id: req.encode+"-cart", "items.name": req.body.name }).then(result => {
        //If item is already in the cart
        if (result != null) {
          //Get item index
          var item_index = result.items.findIndex(obj => obj.name === req.body.name);
          //console.log(result.items[item_index].name);
          //If removing items AND removing all or "more" of the item than currently in cart just remove the item altogether
          if (req.body.quantity < 0 && result.items[item_index].quantity <= Math.abs(req.body.quantity)) {
            var myquery = { _id: req.encode+"-cart" };
            var newvalues = {
              $pull: {
                items: {
                  "name": req.body.name
                }
              }
            };
            
            client.db('API').collection('Cart').updateOne(myquery, newvalues, { upsert: true }, function (err,result){
              //console.log(result)
              if (err) {
                res.status(INTERNAL_SERVER_ERROR).send(err);
              } else {
                res.status(OK).send("Record Removed");
              }
              return client.close();
            })
          } else {
            //Else not removing items OR removing some but not all
            var myquery = { _id: req.encode+"-cart", "items.name": req.body.name };
            var newvalues = {
              $set: {
                "items.$.name": req.body.name,
                "items.$.discount": req.body.discount,
                "items.$.photo": req.body.photo,
                "items.$.price": req.body.price,
                "items.$.region": req.body.region,
              },
              $inc: {
                "items.$.quantity": req.body.quantity
              }
            };
            
            client.db('API').collection('Cart').updateOne(myquery, newvalues, { upsert: true }, function (err,result){
              //console.log(result)
              if (err) {
                res.status(INTERNAL_SERVER_ERROR).send(err);
              } else {
                res.status(OK).send("Record Updated");
              }
              return client.close();
            })
          }
        } else if (req.body.quantity > 0) {
          //Else item is not already in cart so make sure not removing items
          var myquery = { _id: req.encode+"-cart" };
          var newvalues = {
            $addToSet: {
              items: {
                name: req.body.name,
                discount: req.body.discount,
                photo: req.body.photo,
                price: req.body.price,
                region: req.body.region,
                quantity: req.body.quantity
              }
            }
          };
          
          client.db('API').collection('Cart').updateOne(myquery, newvalues, { upsert: true }, function (err,result){
            if (err)
              res.status(INTERNAL_SERVER_ERROR).send(err);
            else {
              res.status(OK).send("Record Added");
            }
            return client.close();
          })
        } else {
          res.status(BAD_REQUEST).send("Bad Request: NO ITEM TO REMOVE FROM CART");
        }
      })
    });
  }
});

//Clear all items from Current User's Shopping Cart (POST)
app.post('/v1/user/profile/clearcart',function(req,res){
  console.log("post cart: " + req.encode+"-cart");
    client = new MongoClient(url, { useNewUrlParser: true, useUnifiedTopology: true });
    client.connect().then(() => {    
      //Check if cart already exists
      client.db('API').collection('Cart').findOne({_id: req.encode+"-cart"}).then(result => {
        //If cart already exists
        if (result != null) {
          var myquery = { _id: req.encode+"-cart" };
          var newvalues = {
            $set: {
              items: []
            }
          };
          //client.db('API').collection('Cart').remove(myquery, function (err,result){
          //Clear all the items in the cart
          client.db('API').collection('Cart').updateOne(myquery, newvalues, {upsert: true}, function (err,result){
            if (err) {
              res.status(INTERNAL_SERVER_ERROR).send(err);
            } else {
              res.status(OK).send("Record Removed");
            }
            return client.close();
          })
        } else {
          res.status(BAD_REQUEST).send("Bad Request: NO CART TO CLEAR");
        }
      })
    });
});

//Listener Setup
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
