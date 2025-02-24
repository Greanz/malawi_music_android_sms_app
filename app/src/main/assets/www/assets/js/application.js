    /*
        Owen Kalungwe
        25-07-19
    */
    const APPLICATION_ID= "MALAWI-MUSIC";

    function getValue(el){
        return $(el).val();
    }

    function putValue(el,value){
        return $(el).val(value);
    }

    function empty(value){
        if(value=="" || typeof(value)==null) return true;
        return false;
    }

    /*
        Convert json Obj to string
    */
    function jsonString(JSONObj){
        return JSON.stringify(JSONObj);
    }

    /*
        Convert string to json
    */
    function toJson(String){
        return $.parseJSON(String);
    }

    /*
        @return true if is valid phone number
        @return false if is not valid phone number
    */
    function isPhoneNumber(phone){
        var check = /^\d{10}$/;
        if(phone.length<10) return false;
        if(phone.match(check))
            return true
        return false
    }


    /*Validate Email
    */
    function isEmail(email) {
        var format = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        if(email.match(format)) return true;
        return false;
    }
    /*
        Redirect
        @after int time in millseconds
    */
    function redirect(URL,after,target){
        if(target != null || after !="undefined"){
            //return window.open(URL,target);
        }
        if(after == null || after =="undefined"){
            return window.location.href = URL;
        }
        setTimeout( function rd(){
            window.location.href = URL;
        },after);
    }

    function replaceAll(str,src,_with){
        var array = str.trim().split(src), newStr='';
        for (var i = 0; i < array.length; i++) {
            if(typeof(array[i]) != "undefined"){
                if(i+1 == array.length){
                    newStr += array[i];
                }
                else
                    newStr += array[i]+_with;
            }
        }
        if(newStr != '')return (newStr);
        return (str);
    }

    function isNumeric(number){
        var numbers = /^[0-9]+$/;
        if(number.match(numbers)){
            return true;
        }
        return false;
    }

    function getCharAt(string,position){
        return string.substring(0,position);
    }

    function urldecode(str) {
        return decodeURIComponent((str+'').replace(/\+/g, '%20'));
    }

    function onlyNums(string){
        string = string.replace(/\D/g,'');
        return string;
    }

    function getParam(ParamID){
        var myAddress = window.location.href, url=null,p=null;
        url = new URL(myAddress);
        p = url.searchParams.get(ParamID);
        if(p==null || p=="") return null;
        return p;
    }

    /*
    *
    * */
    function isURL(url) {
        var urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
        return urlregex.test(url);
    }

    function move(arr, val) {
        var j = 0;
        for (var i = 0, l = arr.length; i < l; i++) {
            if (arr[i] !== val) {
                arr[j++] = arr[i];
            }
        }
        arr.length = j;
    }

    function dbPut(k,v){
        return localStorage.setItem(APPLICATION_ID+'.'+k,v);
    }

    function dbGet(k){
        var v= localStorage.getItem(APPLICATION_ID+'.'+k);
        if(v==null) return null;
        return v;
    }

    function dbRemove(k){
        return localStorage.removeItem(APPLICATION_ID+'.'+k);
    }

    function dbClear(){
        return localStorage.clear();
    }

    function strHas(str,has){
        var pattern = new RegExp(has);
        if(pattern.test(str)==true) return true;
        return false;
    }

    function getFabTop(v=2){
        let h = Math.floor(window.innerHeight/v);
        return h;
    }

    function toast(message){
        if(message==='')return;
        $(document).ready(function () {
            let toast = document.createElement('div');
            toast.className = 'toast';
            let html = "<div>"+message+"</div>";
            toast.innerHTML = html;
            toast.onclick   = function (){
                toast.style.display = 'none';
            }
            let start = setTimeout(function f(){
                toast.style.display='none';
            },4000);
            start;
            return document.body.appendChild(toast);
        });
        return;
    }

    function guidGenerator() {
        var S4 = function() {
            return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
        };
        return (S4()+S4()+""+S4()+""+S4()+""+S4()+""+S4()+S4());
    }

    function shuffle(array) {
        var currentIndex = array.length, temporaryValue, randomIndex;

        // While there remain elements to shuffle...
        while (0 !== currentIndex) {

            // Pick a remaining element...
            randomIndex = Math.floor(Math.random() * currentIndex);
            currentIndex -= 1;

            // And swap it with the current element.
            temporaryValue = array[currentIndex];
            array[currentIndex] = array[randomIndex];
            array[randomIndex] = temporaryValue;
        }

        return array;
    }

    function pop_up(title,msg,positiveBtn,negativeBtn,data){
        $(document).find('#pop-over').detach();
        let template= '<div class="container" data-value="'+data+'">'+
            '<title>'+title+'</title>'+
            '<msg>'+msg+'</msg>'+
            '<myfooter>'+
            '<myBtn class="negativeBtn">'+negativeBtn+'</myBtn>'+
            '<myBtn class="positiveBtn">'+positiveBtn+'</myBtn>'+
            '</myfooter>'+
            '</div>';
        let pop = document.createElement('div');
        pop.className = 'pop-over';
        pop.id = 'pop-over';
        pop.innerHTML = template;
        pop.onclick = function(){
            $('body').click(function(e){
                if ($(e.target).closest(".pop-over .negativeBtn").length) {
                    $('body').css('overflow','');
                    return pop.style.display='none';
                }
                if ($(e.target).closest(".pop-over .container").length) {
                    return;
                }
                $('body').css('overflow','');
                return pop.style.display='none';
            });
        };
        $('body').css('overflow','hidden');
        document.body.appendChild(pop);
    }

    $(document).ready(function(){
        let menu = '<div class="header">'+
            '<a href="#" class="back"><img src="assets/images/favicon2.png"></a>'+
            '<a href="messages.html?mid=m" id="m"><span>MESSAGES</span><span><i class="zmdi zmdi-inbox"></i></span></a>'+
            '<a href="filters.html?mid=f" id="f"><span>FILTERS</span><span><i class="zmdi zmdi-select-all"></i></span></a>'+
            '<a href="settings.html?mid=s" id="s"><span>SETTINGS</span><span><i class="zmdi zmdi-settings"></i></span></a>'+
            '</div>';
        $('body').prepend(menu);
        if(getParam('mid')!=null){
            $('#'+getParam('mid')).addClass('header-a-hovered').attr('href','#');
            $('.header .back').html("<i class='zmdi zmdi-arrow-back'></i>").click(function(){
               redirect('index.html');
            });
        }
        window.sessionStorage.clear();
    });

    function inArray(needle, haystack) {
        var length = haystack.length;
        for(var i = 0; i < length; i++) {
            if(haystack[i] == needle) return true;
        }
        return false;
    }