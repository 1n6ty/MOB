let inpFile = document.getElementsByName('profile_photo')[0],
    btnInpFile = document.getElementsByClassName('photoInput')[0],
    profile_img = document.getElementsByClassName('profile_img')[0],
    nav_img = document.querySelector('a.user img'),
    pers_form = document.getElementsByClassName('persInfo')[0];

inpFile.onchange = (e) => {
    if(e.target.files[0] != undefined){
        let reader = new FileReader();
        loading.classList.add('active');
        reader.onload = (event) => {
            if(['data:image/jpg', 'data:image/jpeg', 'data:image/png'].includes(event.target.result.split(';')[0])){
                profile_img.src = event.target.result; 
                nav_img.src = event.target.result;
                let formData = new FormData();
                formData.append('files', e.target.files[0]);
                fetch("http://192.168.0.180:8000/rules/editUser/", {
                    method: 'PUT',
                    body: formData,
                }).then((value) => {
                    loading.classList.remove('active');
                }).catch((error) => {
                    loading.classList.remove('active');
                });
            }
        };
        reader.readAsDataURL(event.target.files[0]);
    }
};

btnInpFile.onclick = (event) => {
    inpFile.click();
};

pers_form.onsubmit = (event) => {
    event.preventDefault();
    fetch("http://192.168.0.180:8000/rules/editUser/", {
        method: 'PUT',
        body: new FormData(document.forms.persInfo),
    }).catch((error) => ("Something went wrong!", error));
};