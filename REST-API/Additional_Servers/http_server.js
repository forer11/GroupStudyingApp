import http from 'http';

const server = http.createServer((req, res) => {
    res.write('Hello Roy!\n');

    setTimeout(() => {
        res.write('Delayed Response!\n');
        res.end();
    }, 3000);
});

server.listen(8080);