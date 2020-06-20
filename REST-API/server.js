import config from './config';
import apiRouter from './api';
import express from 'express';
const multer = require('multer');
const upload = multer({dest: __dirname + '/uploads/images'});
const server = express();

server.set('view engine', 'ejs');

server.get('/no_need_that', (req, res) => {
  res.send("no need that because we have express - put a file inside public dir instead & express will render it for us :)!")
});


server.get('/', (req, res) => {
    res.render('index', {
    content_from_server: '<br /><br /><br /><br /><br />  i am a content from <b><u>THE SERVER</b></u>'
    });
});


server.use('/api', apiRouter);
server.use(express.static('public'));



server.listen(config.port, () => {
    console.info('Express listening on port:', config.port);
});

 







