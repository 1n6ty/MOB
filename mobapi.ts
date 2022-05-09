//--------------------------------------------------
// Error messages with status codes:
// 'msg': 'bad_request', status - 400
// 'msg': 'not_found' | 'session_time_expired', status - 404
// 'msg': 'token_corrupted', status - 403
//--------------------------------------------------
// With every response you get {'status_code': code}
//--------------------------------------------------
function auth(login: string, password: string): { // login is a phone_number only!
    'response': {
        'refresh': string,
        'token': string
    } //status 200
    'msg': 'error' //status 400, 404
}{return;}

function getLocations(token: string): {
    'response': {
            'country': string,
            'city': string,
            'district': string,
            'house': number,
            'id': number
        }[] //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function setLocation(location_id: number, token: string): {
    'response': {
        'token': string
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function getMarks(token: string): {
    'response': {
        '*id*': { // '*id*' - integer (id of post)
            'x': number // float
            'y': number // float
        },
        //...
    } //status 200
    //---------------------------------------------
    // if (x, y) == -1 -> this mark has no point
    //---------------------------------------------
    'msg': 'error' //status 400, 403, 404
}{return;}

function getPost(post_id: number, token: string): { // post_id -> real id of post
    'response': {
        'id': number,
        'date': string,
        'reactions': {
            '*reaction*': number // like '😂': 10
            //...
        },
        'appreciations': number,
        'appreciated': boolean,
        'data': {
            'img_urls': string[],
            'text': string
        },
        'user': {
            'nick_name': string,
            'name': string,
            'email': string,
            'phone_number': string,
            'id': number
        }
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function deletePost(post_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function post(text: string, imgs_urls: string[], token: string): {
    'response': {
        'id': number
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function getComment(post_id: number, comment_id: number, ind: boolean, token: string): { // post_id -> real id of post; comment_id -> next index of list that you don't have (ind = true)
    'response': {                                                                        // comment_id -> real id of comment (ind = true)
        'user': {
            'nick_name': string,
            'name': string,
            'email': string,
            'phone_number': string,
            'id': number
        },
        'id': number,
        'text': string,
        'date': string,
        'reactions': {
            '*reaction*': number // like '😂': 10
            //...
        },
        'appreciations': number,
        'appreciated': boolean
    } //status 200
    'msg': 'error' //status 400, 403, 404
    //------------------------------------------------------------------
    // Additions in msg errors:
    // 'msg': 'comment_does_not_exist', status - 404
    //------------------------------------------------------------------
}{return;}

function deleteComment(post_id: number, comment_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function editUser(nick_name: string, name: string,
                    email: string, password: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function postInc(post_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function postDec(post_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function commentInc(post_id: number, comment_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function commentDec(post_id: number, comment_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function postReact(post_id: number, reaction: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function postUnreact(post_id: number, reaction: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function commentReact(post_id: number, comment_id: number, reaction: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function commentUnreact(post_id: number, comment_id: number, reaction: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function comment(post_id: number, text: string, token: string): {
    'response': {
        'id': number
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function refreshToken(refreshToken: string, token: string): {
    'response': {
        'refresh': string,
        'token': string
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

interface Token{
    'user_id': number,
    'location_id': number
}