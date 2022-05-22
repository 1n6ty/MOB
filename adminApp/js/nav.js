let html = document.getElementsByTagName('html')[0],
    loading = document.getElementsByClassName('load')[0],
    locker = undefined;

function resize(event){
    if(window.innerWidth <= 512){
        if(html.dataset.mainFull == "false"){
            html.dataset.mainFull = "true";
        }
        if(html.dataset.navShow == "true"){
            html.dataset.navShow = "false";
        }
        if(!locker.classList.contains('d-none')){
            locker.classList.add('d-none');
        }
    } else{
        if(html.dataset.mainFull == "true"){
            html.dataset.mainFull = "false";
        }
        if(html.dataset.navShow == "false"){
            html.dataset.navShow = "true";
        }
        if(locker.classList.contains('d-none')){
            locker.classList.remove('d-none');
        }
    }
};

loading.classList.add('on');
loading.classList.add('active');

window.onload = (event) => {
    let menuBtn = document.getElementsByClassName('menu-btn')[0],
        main = document.getElementsByTagName('main')[0],
        theme = document.getElementsByClassName('theme')[0];
    locker = document.getElementsByClassName('locker')[0];

    resize(event);
    setTimeout(() => {
        loading.classList.remove('active');
        setTimeout(() => {
            loading.classList.remove('on');
        }, 400);
    }, 500);

    menuBtn.onclick = (event) => {
        event.preventDefault();
        html.dataset.navShow = "true";
    };

    main.onclick = (event) => {
        if((window.innerWidth <= 512 || html.dataset.left_nav_lock == 'false') && html.dataset.navShow == "true"){
            html.dataset.navShow = "false";
        }
    };

    locker.onclick = (event) => {
        if(html.dataset.left_nav_lock == 'true'){
            html.dataset.left_nav_lock = 'false';
            html.dataset.mainFull = 'true';
        } else{
            html.dataset.left_nav_lock = 'true';
            html.dataset.mainFull = 'false';
        }
    };

    theme.onclick = (event) => {
        html.dataset.lightMode = (html.dataset.lightMode == 'true') ? 'false': 'true';
    };
}

window.onresize = resize;