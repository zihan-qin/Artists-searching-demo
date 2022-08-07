var axios = require('axios');
axios.defaults.baseURL = 'https://api.artsy.net/api'
var express = require('express');
var app = express();
app.use(require('cors')());
app.use(express.static("artsy"));
var qs = require('qs');
async function getToken(){
    let token = axios.post('/tokens/xapp_token',
    {client_id:'6c5c00d4bfbe29955f30',client_secret:'def9c8cee5cc3b76b1b2bc5a340e0127'})
    .then(function (value) {
        token = value['data']['token'];
        return token;  
    });
    return token;
}
async function searchres(name){
    var res = getToken()
    .then(function(token){
        var searchurl = `/search?q=${name}&size=10`;
        let searchres = axios.get(searchurl,{
            headers:{
                'content-type':'application/json','X-XAPP-Token':token
        }
    })
    return searchres;
    })
    .then(function(value){
        var sendres = [];
        let searchres = value['data']['_embedded']['results']
        for(i in searchres){
            var each = searchres[i]
            if (each['og_type']=='artist'){
                sendres.push({'name':each['title'],'img':each['_links']['thumbnail']['href'],'id':each['_links']['self']['href'].split('/').pop()})
            }
        };
        return sendres;
    });
return res;
}

async function selectres(id){
    var res = getToken()
    .then(function(token){
        var selecturl = `/artists/${id}`;
        let selectres = axios.get(selecturl,{
            headers:{
                'content-type':'application/json','X-XAPP-Token':token
        }
    })
    return selectres;
    })
    .then(function(value){
        let selectres = value['data'];
        var sendres = {'name':selectres['name'],'birthday':selectres['birthday'],'deathday':selectres['deathday'],'nationality':selectres['nationality'],'biography':selectres['biography']};
        return sendres   
        });
    return res;
    }

 
  

    async function epres(id){
        var res = getToken()
        .then(function(token){
            var epurl = `/artworks?artist_id=${id}&size=10`;
            let epres = axios.get(epurl,{
                headers:{
                    'content-type':'application/json','X-XAPP-Token':token
            }
        })
        return epres;
        })
        .then(function(value){
            var sendres = [];
            let epres = value['data']['_embedded']['artworks'];
            for(i in epres){
                var each = epres[i];
                sendres.push({'id':each['id'],'title':each['title'],'date':each['date'],'img':each['_links']['thumbnail']['href']})
            };
            return sendres;  
            })
        return res;
        };

    async function genesres(id){
        var res = getToken()
        .then(function(token){
            var genesurl = `/genes?artwork_id=${id}&size=5`;
            let genesres = axios.get(genesurl,{
                headers:{
                    'content-type':'application/json','X-XAPP-Token':token
                }
            })
        return genesres;
        })
        .then(function(value){
            var sendres = [];
            let genesres = value['data']['_embedded']['genes'];
            for(i in genesres){
                var each = genesres[i];
                sendres.push({'title':each['name'],'img':each['_links']['thumbnail']['href']})
            };
            return sendres;  
            })
        return res;
        };

app.get("/search/:name",(req,res) => {
    var name = req.params['name'];
    var res_search = searchres(name)
    .then(value =>{
        res.send(value);
    })  
    })


app.get("/select/:id",(req,res) => {
    var id = req.params['id'];
    var res_select = selectres(id)
    .then(value =>{
        res.send(value);
    })
    })

app.get("/endpoints/:id",(req,res) => {
    var id = req.params['id'];
    var res_ep = epres(id)
    .then(value =>{
        res.send(value);
    })
    })

app.get("/genes/:id",(req,res) => {
    var id = req.params['id'];
    var res_genes = genesres(id)
    .then(value =>{
        res.send(value);
    })
    })

app.listen(8080, () => {
    console.log('success,8080');
});