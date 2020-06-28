import express from 'express';
import fs from 'fs';
const router = express.Router();

// send the next Json as a rest-api respond to the home: '/' 
// of the api directory: localhost:8080/api

router.get('/', (req, res) => {
    res.send({ data1: [] , data2: [1, 2]});
  });

  router.get('/roy', (req, res) => {
    res.send({ data1: [] , data2: [0, 0]});
  });




export default router;

