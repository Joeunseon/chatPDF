/**
 * main.js - Chat Room Application
 */

// url 관리
const API_ENDPOINTS = {
    fileConfig: '/api/file/config',
    rooms: '/api/rooms',
    fileUpload: '/api/file',
    fileDownload: '/api/file/',
    chat: '/api/chat',
    messages: '/api/msgs/',
    updateRoom: '/api/room',
    deleteRoom: '/api/room/',
};

// Element detach 관리
const elements = {
    divPdfDetach : $('.divPdf').detach(), // pdf 페이지
    spanAiDetach : $('.spanAi').detach(), // 첫 요약 및 예시
    divHDetach : $('.divH').detach(),     // 질문
    divADetach : $('.divA').detach(),     // 답변
    divLDetach : $('.divL').detach(),      // chat loading
    chatRoomDetach : $('.chat-room').detach() // chat room
};

let maxRequestSize; // 서버로 보낼수있는 파일 최대 크기
let maxFileSize;	// 단일 파일 최대 크기

$(document).ready(function () {

    fileInit();

    roomsInit();

    bindEventListeners();
});

// 이벤트 리스너 바인딩
function bindEventListeners() {
    // 파일 업로드시
    $('#inputFile').on('change', function() {
        uploadFile(this.files);
    });

    // 파일 업로드 버튼 클릭시
    $('.ant-upload-btn').on('click', function() {
        let fileInput = document.getElementById('inputFile');
        fileInput.click();
    });

    // 채팅 엔터시
    $('.ant-input').on('keypress', function(e) {
        if (e.which == 13) {
            e.preventDefault();
            askAi();
        }
    });
}

// 파일 설정
function fileInit() {
    fn_fetchGetData(API_ENDPOINTS.fileConfig)
        .then(data => {
            maxRequestSize = data.maxRequestSize;
            maxFileSize = maxFileSize;
        })
        .catch(fn_handleError);
}

// 채팅방 초기화
function roomsInit() {
    $('.chat-rooms').empty();

    fn_fetchGetData(API_ENDPOINTS.rooms)
        .then(data => {
            if ( data.resultCnt > 0 ) {
                renderChatRooms(data.resultList);
            }
        })
        .catch(fn_handleError);
}

// 채팅방 렌더링
function renderChatRooms(roomList) {
    roomList.forEach(item => {
        const roomElement = createRoomElement(item);
        $('.chat-rooms').append(roomElement);
    });
}

// 채팅방 요소 생성
function createRoomElement(item) {
    let roomClone = elements.chatRoomDetach.clone();

    roomClone.find('span').text(item.title);

    // 채팅방 클릭시
    roomClone.on('click', () => handleRoomClick(item));

    // 옵션 버튼 클릭시
    roomClone.find('.options-button').on('click', (e) => toggleOptionsMenu(e, roomClone));

    // 옵션 메뉴 버튼 클릭시
    roomClone.find('.options-menu').on('click', (e) => e.stopPropagation());

    // 옵션 메뉴 수정버튼 클릭시
    roomClone.find('.options-upd').on('click', (e) => handleRoomUpdate(e, roomClone, item));

    // 삭제 버튼 클릭시
    roomClone.find('.options-del').on('click', (e) => handleRoomDelete(e, roomClone, item.roomSeq));
    
    return roomClone;
}

// 채팅방 클릭시 처리
function handleRoomClick(item) {
    // 데이터 설정
    $('.divMiddle').find('h1').text(item.title);
    $('#sourceId').val(item.apiId);
    $('#roomSeq').val(item.roomSeq);

    // 파일 뷰어 페이지 초기화
    $('#pdfContainer').empty();
    // 전체 화면 로딩 
    $('#loadingDiv').show();

    // 채팅방 내용 가져오기
    getContent(item.fileSeq);
}

// 채팅방 옵션 메뉴 토글
function toggleOptionsMenu(e, roomClone) {
    // 이벤트 버블링 방지
    e.stopPropagation();

    const menu = roomClone.find('.options-menu');

    // 기존 열린 메뉴 닫기
    $('.options-menu').not(menu).hide();

    // 현재 메뉴 토글
    menu.toggle();

    // 메뉴 외부 클릭 시 닫기
    $(document).on('mousedown.options-menu', (event) => {
        if (!$(event.target).closest(menu).length && !$(event.target).is('.options-button')) {
            menu.hide();
            $(document).off('mousedown.options-menu'); // 이벤트 해제
        }
    });
}

// 채팅방 업데이트
function handleRoomUpdate(e, roomClone, item) {
    // 이벤트 버블링 방지
    e.stopPropagation();

    const currentTitle = item.title;
    const input = $('<input type="text" class="title-input">')
        .val(currentTitle)
        .css({ width: '100%', padding: '4px', boxSizing: 'border-box'});

    // 기존 제목을 입력창으로 변경
    roomClone.find('span').replaceWith(input);
    input.focus();
    input.on('click', (e) => e.stopPropagation());

    // 입력창 포커스를 잃었을 때 처리
    input.on('blur', () => {
        const newTitle = input.val().trim() || currentTitle; // 입력값 없을 시 기존 제목 유지

        if ( newTitle != currentTitle ) {
            updRoom(item.roomSeq, newTitle, isUpdated => {
                if ( isUpdated ) {
                    input.replaceWith(`<span style="color: white;">${newTitle}</span>`);
                    item.title = newTitle; // 데이터 업데이트
                }
            });
        } else {
            input.replaceWith(`<span style="color: white;">${newTitle}</span>`);
            item.title = newTitle; // 데이터 업데이트
        }
    });
}

// 채팅방 삭제
function handleRoomDelete(e, roomClone, roomSeq) {
    // 이벤트 버블링 방지
    e.stopPropagation();

    if ( confirm('해당 채팅방을 삭제하시겠습니까?') ) {
        delRoom(roomSeq, isDeleted => {
            if (isDeleted) 
                roomClone.remove();
        });
    }
}

// 파일 업로드시
function uploadFile(fileObject) {
    const file = fileObject[0];
    const fileName = file.name;
    const fileSize = file.size;
    const fileExtension = fileName.split('.').pop();

    if ( !['pdf'].includes(fileExtension) ) {
        alert(`pdf 파일만 업로드 가능합니다. (${fileName})`);
        return;
    }

    if ( fileSize <= 0 ) {
        alert('파일크기가 0kb 입니다.');
        return;
    }

    if ( fileSize > maxFileSize ) {
        alert(`업로드 가능한 파일의 최대 크기는 ${maxFileSize} 입니다.`);
        return;
    }

    // 파일 뷰어 제목 설정
    $('.divMiddle').find('h1').text(fileName);
    // 파일 뷰어 페이지 초기화
    $('#pdfContainer').empty();
    // 전체 화면 로딩 
    $('#loadingDiv').show();

    // 파일 뷰어
    renderPDF(file);
    // 파일 업로드
    fileUpload(file);
}

// 파일 업로드
function fileUpload(file) {
    // id 초기화
	$('#sourceId').val('');

    let formData = new FormData();
    formData.append('file', file);

    fn_fetchPostData(API_ENDPOINTS.fileUpload, formData)
        .then(data => {
            if ( data.sourceId ) {
                $('#sourceId').val(data.sourceId);
                $('#roomSeq').val(data.roomSeq);
                
                // 초기 질문 생성
                firstAskAi();
                // 채팅방 목록 취득
                roomsInit();
            } else {
                $('#loadingDiv').hide();
            }
        })
        .catch(fn_handleError);
}

// 초기 질문 생성
function firstAskAi() {
    restChatUI();

    let sourceId = $('#sourceId').val();
    let roomSeq = $('#roomSeq').val();

    if ( sourceId == '' || roomSeq == '' ) {
        alert('처리 중 문제가 발생하였습니다.');
        console.log('Error function : firstAskAi');
        return;
    }

    let params = {
        "apiId": sourceId,
	  	"sendType": 'FIRST',
        "roomSeq": roomSeq
    };

    // ChatPDF 답변 취득
	getAnswer(params, 'first');
}

// 답변 취득
function getAnswer(params, type) {
    const header = {'Content-Type': 'application/json'};

    fn_fetchPostData(API_ENDPOINTS.chat, params, header)
        .then(data => processAnswerResponse(data, type))
        .catch(fn_handleError);
}

// 답변 처리
function processAnswerResponse(data, type) {
    const answer = data.content;

    if ( answer ) {
        if ( type === 'first' ) {
            processFirstAnswer(answer);
        } else if ( type === 'ask' ) {
            appendAnswerToChat(answer);
        }

        // 전체 로딩 hide
        $('#loadingDiv').hide();
        // 로딩 삭제
        $('.divL').remove();
    }
}

// 초기 질문에 대한 답변 처리
function processFirstAnswer(answer) {
    const lines = answer.split('\n');
    let exampleStartIndex = lines.length;
    
    $('#chatAi').empty();

    lines.forEach((line, index) => {
        if ( line.includes('예시 질문') || line.includes('질문 예시') ) {
            exampleStartIndex = index;
        }

        if ( index > exampleStartIndex && index !== 0 ) {
            if ( line.trim() ) {
                const spanAiClone = elements.spanAiDetach.clone();
                spanAiClone.find('.exAsk').text(line);
                $('#chatAi').append(spanAiClone);
            }
        } else if ( index === exampleStartIndex && index !== 0 ) {
            $('#chatAi').append('<br>' + line);
        } else {
            $('#chatAi').append(line + '<br>');
        }
    });

    // pdf 뷰어
    $('.divMiddle').show();
    // chat 
    $('.divRight').show();
}

/**
 * 일반 질문에 대한 답변 추가
 * @param {string} answer - ChatPDF 답변
 */
function appendAnswerToChat(answer) {
    const divAClone = elements.divADetach.clone();
	divAClone.find('.chat-message').html(answer.replace(/(?:\r\n|\r|\n)/g, '<br>'));
	$('#chatUl').append(divAClone);
}

// 예시 질문 클릭시
function exAsk(el) {
    const content = $(el).closest('.spanAi').find('.exAsk').text().trim();
    const sourceId = $('#sourceId').val();
    const roomSeq = $('#roomSeq').val();
    
    if ( !sourceId || !content || !roomSeq ) {
        alert('처리 중 문제가 발생하였습니다.');
        console.log('Error function : exAsk');
        return;
    }

    let params = {
        "apiId": sourceId,
        "sender": 'user',
	  	"sendType": 'OTHER',
        "roomSeq": roomSeq,
        "content": content
    };
    
    // 질문을 채팅에 추가
    appendQuestionToChat(content);
    
    // 로딩 
    chatLoading();
    
    // ChatPDF 답변 취득
    getAnswer(params, 'ask');
}

/**
 * 질문을 채팅에 추가
 * @param {string} content 
 */
function appendQuestionToChat(content) {
    const divHClone = elements.divHDetach.clone();
    divHClone.find('.chat-message').text(content);
    $('#chatUl').append(divHClone);
}

// 사용자 질문
function askAi() {
    const sourceId = $('#sourceId').val();
    const roomSeq = $('#roomSeq').val();
    const content = $('.ant-input').val().trim();
    
    if ( !sourceId || !roomSeq ) {
        alert('처리 중 문제가 발생하였습니다.');
        console.log('Error function : askAi');
        return;
    }
    
    if ( !content ) {
        alert('질문 내용을 작성해주세요');
        $('.ant-input').val('');
        return;
    }

    let params = {
        "apiId": sourceId,
        "sender": 'user',
	  	"sendType": 'OTHER',
        "roomSeq": roomSeq,
        "content": content
    };
    
    // 질문란 초기화
    $('.ant-input').val('');
    // 질문을 채팅에 추가
    appendQuestionToChat(content);
    
    // 로딩 
    chatLoading();
    // ChatPDF 답변 취득
    getAnswer(params, 'ask');
}

// 챗 로딩
function chatLoading() {
    const divLClone = elements.divLDetach.clone();
    $('#chatUl').append(divLClone);
}

// pdf 뷰어
function renderPDF(file) {
    let fileReader = new FileReader();

    fileReader.onload = function(e) {
        let arrayBuffer = e.target.result;

        // PDF.js 설정
        pdfjsLib.GlobalWorkerOptions.workerSrc = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.12.313/pdf.worker.min.js";

        // PDF 파일 로드
        pdfjsLib.getDocument({ data: arrayBuffer }).promise.then(function(pdf) {
            // 총 페이지 수 가져오기
            let totalPages = pdf.numPages;
            let heightTotal = 0;

            // 모든 페이지 렌더링
            for (let pageNumber = 1; pageNumber <= totalPages; pageNumber++) {
                pdf.getPage(pageNumber).then(function(page) {
                    // 페이지 처리
                    let divPdfClone = elements.divPdfDetach.clone();
                    
                    // 페이지 설정
                    let viewport = page.getViewport({ scale: 1 });
                    heightTotal += viewport.height;
                    
                    let index = page._pageIndex;
                    
                    divPdfClone.find('.rpv-core__inner-page').attr('aria-label', "Page " + (page._pageIndex+1));
                    divPdfClone.find('.rpv-core__inner-page').css('transform', 'translateY('+(viewport.height*index)+'px)');
                    divPdfClone.find('.rpv-core__inner-page').css('height', viewport.height+'px');
                    
                    divPdfClone.find('.rpv-core__page-layer').attr('data-testid', 'core__page-layer-'+page._pageIndex);
                    divPdfClone.find('.rpv-core__page-layer').attr('data-virtual-index', page._pageIndex);
                    divPdfClone.find('.rpv-core__page-layer').css('height', viewport.height+'px');
                    divPdfClone.find('.rpv-core__page-layer').css('width', viewport.width+'px');

                    let pageContent = divPdfClone.find('.rpv-core__page-layer').find("div");

                    // 페이지 내용 추가
                    let canvas = document.createElement("canvas");
                    pageContent.append(canvas);

                    // 페이지 렌더링
                    canvas.height = viewport.height;
                    canvas.width = viewport.width;

                    let renderContext = {
                        canvasContext: canvas.getContext("2d"),
                        viewport: viewport
                    };
                    page.render(renderContext);

                    // 페이지 컨테이너에 추가
                    $('#pdfContainer').append(divPdfClone);
                });
            }

            // 스크롤 설정
            //$('#pdfContainer').css('height', (700*totalPages)+'px');
            $('#pdfContainer').css('height', heightTotal+'px');
        });
    };

    fileReader.readAsArrayBuffer(file);
}

function getContent(fileSeq) {
    // 초기화
    restChatUI();

    fn_fetchGetBlob(`${API_ENDPOINTS.fileDownload}${fileSeq}`)
        .then(blob => {
            renderPDF(blob);
            // pdf 뷰어
            $('.divMiddle').show();
        })
        .catch(fn_handleError);

    fn_fetchGetData(`${API_ENDPOINTS.messages}${$('#roomSeq').val()}`)
        .then(data => processMessageResponse(data))
        .catch(fn_handleError);
}

// 채팅 UI 초기화
function restChatUI() {
    // 초기화
    $('#chatUl').children(':first-child').nextAll().remove();
    // 질문 입력란 초기화
    $('.ant-input').val('');
}

/**
 * 메시지 응답처리
 * @param {Object} data - 채팅 메시지 데이터
 */
function processMessageResponse(data) {
    if ( data.resultCnt > 0 ) {
        data.resultList.forEach(item => {
            if ( item.sendType == 'FIRST' ) {
                $('#chatAi').empty();
                processFirstAnswer(item.content);
            } else if ( item.sender == 'assistant' ) {
                appendAnswerToChat(item.content)
            } else {
                appendQuestionToChat(item.content);
            }
        });

        // chat 
        $('.divRight').show();
        // 전체 로딩 hide
        $('#loadingDiv').hide();
        // 로딩 삭제
        $('.divL').remove();
    } else {
        firstAskAi();
    }
}

/**
 * 채팅방 제목 수정
 * @param {number} roomSeq - 채팅방 SEQ
 * @param {string} title - 채팅방 제목
 * @param {Function} callback - 수정 완료 후 실행할 콜백 함수
 */
function updRoom(roomSeq, title, callback) {
    const header = {
        'Content-Type': 'application/json',
    };

    const params = {
        'roomSeq': roomSeq,
        'title': title
    };

    fn_fetchPatchData(API_ENDPOINTS.updateRoom, params, header)
        .then(isUpdated => {
            if ( !isUpdated ) 
                alert('채팅방 제목 수정에 실패했습니다.');
            
            callback(isUpdated);
        })
        .catch(error => {
            fn_handleError(error);
            callback(false);
        });
}

/**
 * 채팅방 삭제
 * @param {number} roomSeq - 채팅방 SEQ
 * @param {Function} callback  - 삭제 완료 후 콜백 함수
 */
function delRoom(roomSeq, callback) {
    fn_fetchDeleteData(`${API_ENDPOINTS.deleteRoom}${roomSeq}`)
        .then(isDeleted  => {
            if ( isDeleted && roomSeq === parseInt($('#roomSeq').val(), 10) ) {
                resetChatView();
            }

            callback(isDeleted);
        })
        .catch(error => {
            fn_handleError(error);
            callback(false);
        });
}

// 채팅 UI 초기화
function resetChatView() {
    $('.divMiddle').hide();
    $('.divRight').hide();
    $('#sourceId').val('');
    $('#roomSeq').val('');
}