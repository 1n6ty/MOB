let myCarousel = document.querySelector('#post-carousel');
let carousel = new bootstrap.Carousel(myCarousel);

let back_btn = document.getElementsByClassName('back-btn')[0];
back_btn.onclick = (e) => {
    window.close();
};