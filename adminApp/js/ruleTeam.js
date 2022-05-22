let btn_add_new_user = document.querySelector('.memberTeam .btn-add'),
    memberTeam_body = document.querySelector('body > main > div > div:nth-child(3) > div > div.card.h-100-card > div');

function htmlToElement(htmlText) {
    var template = document.createElement('template');
    htmlText = htmlText.trim();
    template.innerHTML = htmlText;
    return template.content.firstChild;
}

let new_member = htmlToElement(
    `<div class="memberTeamNew mb-3 pb-3 border-bottom border-separator-light position-relative addition">
        <div class="row g-0">
            <div class="col-auto">
                <img class="card-img rounded-xl sh-6 sw-6" alt="team member" src="img/1.jpg">
            </div>
            <div class="col">
                <div class="row position-relative d-flex h-100 align-items-center justify-content-between">
                    <div class="col">
                        <input type="email" placeholder="Enter Email" class="form-control" aria-describedby="emailHelp">
                    </div>
                    <div class="col d-flex justify-content-end btn-container">
                        <button class="cncl btn btn-outline-secondary btn-sm ms-1">Cancel</button>
                        <button class="subm btn btn-submit btn-sm ms-1">Submit</button>
                    </div>
                </div>
            </div>
        </div>
    </div>`),
    blur = htmlToElement(
    `<div class="blured">
        <div></div>
        <img src="img/preloader.gif">
    </div>`);

let queue_members = true;

new_member.querySelector('.cncl').onclick = (event) => {
    memberTeam_body.removeChild(memberTeam_body.firstChild);
    queue_members = true;
};
new_member.querySelector('.subm').onclick = (event) => {

};

btn_add_new_user.onclick = (event) => {
    if(queue_members){
        memberTeam_body.insertBefore(new_member, memberTeam_body.firstChild);
        queue_members = false;
    }
};