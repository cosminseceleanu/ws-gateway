const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()

server.use(middlewares);
server.use(jsonServer.bodyParser);

server.get('/echo', (req, res) => {
    res.jsonp(req.query)
})

server.post('/events/connected', (req, res) => {
    res.jsonp(req.body)
});

server.post('/events/disconnected', (req, res) => {
    res.jsonp(req.body)
});

server.post('/events/default', (req, res) => {
    res.jsonp(req.body)
});

server.post('/events/custom/1', (req, res) => {
    res.jsonp(req.body)
});

function getRandomInt(max) {
    return Math.floor(Math.random() * Math.floor(max));
}

//add random delay
server.use((req, res, next) => {
    setTimeout(() => {
        next();
    }, getRandomInt(100));
})
server.use(router);

server.listen(3000, () => {
    console.log('JSON Server is running')
});