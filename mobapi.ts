//--------------------------------------------------
// Error messages with status codes:
// 'msg': 'bad_request', status - 400
// 'msg': 'not_found' | 'session_time_expired', status - 404
// 'msg': 'token_corrupted', status - 403
//--------------------------------------------------
// With every response you get {'status_code': code}
//--------------------------------------------------
function auth(login: string, password: string): { // login is a phone_number or nick or email
    'response': {
        'refresh': string,
        'token': string,
        'user': {
            'id': number,
            'email': string,
            'phone_number': string,
            'full_name': string,
            'nick': string,
            'profile_img_url': string 
        }
    } //status 200
    'msg': 'error' //status 400, 404
}{return;}

function me(token: string): {
    'response': {
        'user': {
            'nick': string,
            'full_name': string,
            'email': string,
            'phone_number': string,
            'id': number,
            'profile_img_url': string
        },
        'current_address': {
            'id': number,
            'country': string,
            'city': string,
            'street': string,
            'house': number
        } | 'none',
        'addresses': {
            'id': number,
            'country': string,
            'city': string,
            'street': string,
            'house': number
        }[] | 'none',
    }
    'msg': 'error' //status 400, 403, 404
}{return;}

function getUserProfile(user_id: number, token: string): {
    'response': {
        'user': {
            'nick': string,
            'full_name': string,
            'email': string,
            'phone_number': string,
            'id': number,
            'profile_img_url': string
        }
    }
    'msg': 'error' //status 400, 403, 404
}{return;}

function setCurrentAddress(location_id: number, token: string): {
    'response': {
        'token': string
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function getMarks(token: string): {
    'response': {
        '*id*': { // '*id*' - integer (id of post)
            'category': 'News' | 'Suggestions',
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
            '*reaction*': [number] // like '😂': [10, 13, ...]
            //...
        },
        'rate': {
            'p': [number], // ids of users
            'm': [number] // ids of users
        },
        'data': {
            'title': string,
            'img_urls': string[],
            'content': string,
            'comment_ids': [number] | 'none' // real ids of comments
        },
        'user': {
            'nick': string,
            'full_name': string,
            'email': string,
            'phone_number': string,
            'id': number,
            'profile_img_url': string
        }
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function deletePost(post_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function post(content: string, img_routes: string[], token: string): {
    'response': {
        'id': number
    } //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function getComment(comment_id: number, token: string): { // comment_id -> real comment_id
    'response': {  
        'id': number,
        'date': string,
        'user': {
            'nick': string,
            'full_name': string,
            'email': string,
            'phone_number': string,
            'id': number,
            'profile_img_url': string
        },
        'data': {
            'content': string,
            'comment_ids': [number] | 'none' // real ids of comments
        },
        'reactions': {
            '*reaction*': [number] // like '😂': [10, 13, ...] 'ids'
            //...
        },
        'rate': {
            'p': [number], // ids of users
            'm': [number] // ids of users
        }
    } //status 200
    'msg': 'error' //status 400, 403, 404
    //------------------------------------------------------------------
    // Additions in msg errors:
    // 'msg': 'comment_does_not_exist', status - 404
    //------------------------------------------------------------------
}{return;}

function deleteComment(comment_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function editUser(new_nick: string | null, new_full_name: string | null,
                    new_email: string | null, new_password: string | null, new_profile_img_route: string | null, token: string): {
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

function commentInc(comment_id: number, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function commentDec(comment_id: number, token: string): {
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

function commentReact(comment_id: number, reaction: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function commentUnreact(comment_id: number, reaction: string, token: string): {
    'response': {} //status 200
    'msg': 'error' //status 400, 403, 404
}{return;}

function comment(content: string, token: string): {
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