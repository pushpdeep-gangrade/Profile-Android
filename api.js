const express = require('express');
var cors = require('cors');
const app = express();
var mysql = require('mysql');
const port = 8080
const jwt = require('jsonwebtoken');

//swagger for documentation
const swaggerJSDoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

app.use(cors());
app.use(express.json());
var bodyParser = require('body-parser');
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }));

//Status encoded
const OK = 200;
const BAD_REQUEST = 400;
const UNAUTHORIZED = 401;
const NOT_FOUND = 404;
const INTERNAL_SERVER_ERROR = 500;


const con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "pushp2209",
  database: "API"
});

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
    "servers" : ["http://104.248.113.55:8080/"]
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
      jwt.verify(req.headers.authorizationkey, 'secret', function(err, decoded) {
        if(err){
          res.status(BAD_REQUEST).send(err.message);
        }
        else{
        next();
      }
      });
    }
} catch(err) {
  res.send(err);
}
});

app.post('/v1/user/login',function(req,res){
  con.query("SELECT emailId,age,fname,lname,address FROM User where emailId='" + req.body.email +"' AND password='" + req.body.password + "';" , function (err, result, fields) {
    if (err)
        res.status(INTERNAL_SERVER_ERROR).send(err);
    if(result.length <= 0)
      res.status(OK).send("Invalid Credentials");
      else{
        var token = jwt.sign({ emailId: req.body.email,}, 'secret',{expiresIn : 60 * 10} );
    res.header("AuthorizationKey", token).status(OK).send(result);}
  });
});

app.get('/v1/user/profile/:email',function(req,res){
  if(!req.params){
  res.status(BAD_REQUEST).send("Bad request Check parameters");}
  else{
    con.query("SELECT emailId,age,fname,lname,address FROM User where emailId='" + req.params.email + "';" , function (err, result, fields) {
      if (err)
      res.status(INTERNAL_SERVER_ERROR).send(err);
      else if(result.length <= 0)
        res.status(OK).send("No record found");
        else{
      res.status(OK).send(result);}
    });}
});

app.post('/v1/user/profile/:email',function(req,res){
  if(req.body == null){
  res.status(BAD_REQUEST).send("Bad request Check parameters or Body");
}
    else{
  const sql = "Update User set password = '"
  + req.body.password + "',age="
  + req.body.age + ",fname='"
  + req.body.firstname + "',lname='"
  + req.body.lastname + "',address='"
  + req.body.address + "' where emailId='" + req.params.email + "';"

  con.query(sql, function (err, result, fields) {
    if (err)
    res.status(INTERNAL_SERVER_ERROR).send(err.sqlMessage);
    else if(result.fieldCount == 0 && result.affectedRows == 0)
    res.status(OK).send("Email id not found in Record");
    else if(result.affectedRows > 0)
      res.status(OK).send("Record Updated");
  });}
});


app.post('/v1/user/signup', (req, res) => {

if(req.body == null)
  res.status(BAD_REQUEST).send("Bad request Check parameters or Body");
  else{
  const sql = "Insert into User(emailId,password,age,fname,lname,address) values ('"
              + req.body.email + "','"
              + req.body.password + "',"
              + req.body.age + ",'"
              + req.body.firstname + "','"
              + req.body.lastname + "','"
              + req.body.address + "');";

  con.query(sql, function (err, result, fields) {
                   if (err){
                     const message = err.sqlMessage;
                     if(message.includes("Duplicate"))
                     res.status(OK).send("Email id already registered");
                     else
                       res.status(INTERNAL_SERVER_ERROR).send(message);
                   }
                   else {
                    if(result.affectedRows >= 1)
                     res.status(OK).send("Signed up Successfully");
                   }
                 });
               }
});

app.post('/logout', (req, res) => {

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
    * /v1/user/profile/{email}:
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
    *       - name: email
    *         in: path
    *         required: true
    *         type: string
    *         description: User's emailId
    *     responses:
    *       200:
    *         description: A successful call returns response with authorizationkey in the header.
    *         schema:
    *           $$ref: '#/definitions/updateProfile'
    *       400:
    *         description: Error code and error message returned in JSON.
    */
