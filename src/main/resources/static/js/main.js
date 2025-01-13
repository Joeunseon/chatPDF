/**
 * main.js
 */

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

    // 파일 업로드시
    $('#inputFile').bind('change', function() {
        uploadFile(this.files);
    });

    // New Chat 클릭시
    $('.ant-upload-btn').on('click', function () {
        let fileInput = document.getElementById('inputFile');
        fileInput.click();
    });

    // 질문 엔터시
    $('.ant-input').keypress(function(e) {
        if (e.which == 13) {
          e.preventDefault();
          askAi();
        }
    });

});

/**
 * 파일 init
 * properties에 설정된 maxRequestSize, maxFileSize 취득
 */
function fileInit() {
    const url = '/api/file/config';

    fn_fetchGetData(url)
        .then(data => {
            maxRequestSize = data.maxRequestSize;
            maxFileSize = maxFileSize;
        })
        .catch(fn_handleError);
}

function roomsInit() {
    const url = '/api/rooms';
    $('.chat-rooms').empty();

    fn_fetchGetData(url)
        .then(data => {
            if ( data.resultCnt > 0 ) {
                data.resultList.forEach(item => {
                    let divClone = elements.chatRoomDetach.clone();

                    divClone.find('span').text(item.title);

                    // 채팅방 클릭시
                    divClone.on('click', () => {
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
                    });

                    // 옵션 버튼 클릭시
                    divClone.find('.options-button').on('click', (e) => {
                        // 이벤트 버블링 방지
                        e.stopPropagation();

                        //divClone.find('.options-menu').show();
                        const menu = divClone.find('.options-menu');

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
                    });

                    // 옵션 메뉴 버튼 클릭시
                    divClone.find('.options-menu').on('click', (e) => {
                        // 이벤트 버블링 방지
                        e.stopPropagation();
                    });

                    divClone.find('.options-upd').on('click', (e) => {
                        // 이벤트 버블링 방지
                        e.stopPropagation();

                        const titleElement = divClone.find('span');
                        const currentTitle = titleElement.text();

                        // 이름 변경을 위한 입력창 생성
                        const input = $('<input type="text" class="title-input">')
                            .val(currentTitle)
                            .css({
                                width: '100%',
                                padding: '4px',
                                boxSizing: 'border-box',
                            });

                        // 기존 제목을 입력창으로 변경
                        titleElement.replaceWith(input);
                        input.focus();

                        // 이벤트 버블링 방지
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
                    });

                    // 삭제 버튼 클릭시
                    divClone.find('.options-del').on('click', (e) => {
                        // 이벤트 버블링 방지
                        e.stopPropagation();

                        if ( confirm('해당 채팅방을 삭제하시겠습니까?') ) {
                            delRoom(item.roomSeq, isDeleted => {
                                if (isDeleted) 
                                    divClone.remove();
                            });
                        }
                    });
                    
                    $('.chat-rooms').append(divClone);
                });
            }
        })
        .catch(fn_handleError);
}

// 파일 업로드시
function uploadFile(fileObject) {
    let files = fileObject;

    if ( fileObject != null ) {
        // 파일 이름 
        let fileName = fileObject[0].name;
        let fileNameArr = fileName.split("\.");
        // 확장자
        let ext = fileNameArr[fileNameArr.length - 1];

        if ($.inArray(ext, [ 'pdf' ]) == -1) {
            alert("pdf 파일만 업로드 가능합니다. ("+fileName+")");
            return;
        }

        let fileSize = fileObject[0].size;
        if ( fileSize <= 0 ) {
            alert('파일크기가 0kb 입니다.');
            return;
        }

        if ( fileSize > maxFileSize ) {
            alert('업로드 가능한 파일의 최대 크기는 ' + maxFileSize + ' 입니다.');
            return;
        }
    
        // 파일 뷰어 제목 설정
        $('.divMiddle').find('h1').text(fileName);
        // 파일 뷰어 페이지 초기화
        $('#pdfContainer').empty();
        // 전체 화면 로딩 
        $('#loadingDiv').show();
        // 파일 뷰어
        renderPDF(fileObject[0]);
        // 파일 업로드
        fileUpload();
    } else {
        alert("ERROR");
    }
}

// 파일 업로드
function fileUpload() {
    // id 초기화
	$('#sourceId').val('');

    let formData = new FormData();
    let fileInput = document.querySelector('input[type="file"]');
    formData.append('file', fileInput.files[0]);

    const url = '/api/file';

    fn_fetchPostData(url, formData)
        .then(data => {
            let sourceId = data.sourceId;
            if ( sourceId != null && sourceId != '' ) {
                $('#sourceId').val(sourceId);
                $('#roomSeq').val(data.roomSeq);
                return true;
            } 
            return false;
        })
        .then ( flag => {
            if ( flag ) {
                // 요약과 질문 예시
                firstAskAi();
                // 채팅방 목록 취득
                roomsInit();
            } else {
                $('#loadingDiv').hide();
            }
        })
        .catch(fn_handleError);

}

// 요약과 질문 예시
function firstAskAi() {
    // 챗 화면 초기화
    $('#chatUl').children(':first-child').nextAll().remove();
    // 질문 입력란 초기화
    $('.ant-input').val('');

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

    const url = '/api/chat';

    const header = {
        'Content-Type': 'application/json'
    };

    fn_fetchPostData(url, JSON.stringify(params), header)
        .then(data => {
            let answer = data.content;

            if ( answer != null && answer != '' ) {
                if ( type == 'first' ) {
                    let strArr = answer.split('\n');
                    let idx = strArr.length;
                    $('#chatAi').empty();

                    for (var i = 0; i < strArr.length; i++) {
                        if ( strArr[i] != '' ) {
                            if ( strArr[i].includes('예시 질문') || strArr[i].includes('질문 예시') ) {
                                idx = i;
                            }
                            
                            if ( i > idx && i != 0 ) {
                                let spanAiClone = elements.spanAiDetach.clone();
                                spanAiClone.find('.exAsk').text(strArr[i]);
                                $('#chatAi').append(spanAiClone);
                            } else if ( i == idx && i != 0 ) {
                                $('#chatAi').append('<br>'+strArr[i]);
                            } else {
                                $('#chatAi').append(strArr[i]+'<br>');
                            }
                        }

                        // pdf 뷰어
			        	$('.divMiddle').show();
			        	// chat 
			        	$('.divRight').show();
                    } 
                } else if ( type == 'ask' ) {
			        let divAClone = elements.divADetach.clone();
			        divAClone.find('.chat-message').html(answer.replace(/(?:\r\n|\r|\n)/g, '<br>'));
			        $('#chatUl').append(divAClone);
                }

                // 전체 로딩 hide
                $('#loadingDiv').hide();
                // 로딩 삭제
                $('.divL').remove();
            }
        })
        .catch(fn_handleError);
}

// 예시 질문 클릭시
function exAsk(el) {
    let content = $(el).closest('.spanAi').find('.exAsk').text().trim();
    let sourceId = $('#sourceId').val();
    let roomSeq = $('#roomSeq').val();
    
    if ( sourceId == '' || content == '' || roomSeq == '' ) {
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
    
    // 질문 등록
    let divHClone = elements.divHDetach.clone();
    divHClone.find('.chat-message').text(content);
    $('#chatUl').append(divHClone);
    
    // 로딩 
    chatLoading();
    
    // ChatPDF 답변 취득
    getAnswer(params, 'ask');
}

// 사용자 질문
function askAi() {
    let sourceId = $('#sourceId').val();
    let roomSeq = $('#roomSeq').val();
    let content = $('.ant-input').val().trim();
    
    if ( sourceId == '' || roomSeq == '' ) {
        alert('처리 중 문제가 발생하였습니다.');
        console.log('Error function : askAi');
        return;
    }
    
    if ( content == '' ) {
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
    // 질문 등록
    let divHClone = elements.divHDetach.clone();
    divHClone.find('.chat-message').text(content);
    $('#chatUl').append(divHClone);
    
    // 로딩 
    chatLoading();
    
    // ChatPDF 답변 취득
    getAnswer(params, 'ask');
}

// 챗 로딩
function chatLoading() {
    // 로딩 
    let divLClone = elements.divLDetach.clone();
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

    const fileUrl = '/api/file/' + fileSeq;
    // 초기화
    $('#chatUl').children(':first-child').nextAll().remove();
    // 질문 입력란 초기화
    $('.ant-input').val('');

    fn_fetchGetBlod(fileUrl)
        .then(blob => {
            renderPDF(blob);
            // pdf 뷰어
            $('.divMiddle').show();
        })
        .catch(fn_handleError);

    const msgUrl = '/api/msgs/' + $('#roomSeq').val();

    fn_fetchGetData(msgUrl)
        .then(data => {
            if ( data.resultCnt > 0 ) {

                data.resultList.forEach(item => {
                    let answer = item.content;

                    if ( item.sendType == 'FIRST' ) {
                        let strArr = answer.split('\n');
                        let idx = strArr.length;
                        $('#chatAi').empty();
    
                        for (var i = 0; i < strArr.length; i++) {
                            if ( strArr[i] != '' ) {
                                if ( strArr[i].includes('예시 질문') || strArr[i].includes('질문 예시') ) {
                                    idx = i;
                                }
                                
                                if ( i > idx && i != 0 ) {
                                    let spanAiClone = elements.spanAiDetach.clone();
                                    spanAiClone.find('.exAsk').text(strArr[i]);
                                    $('#chatAi').append(spanAiClone);
                                } else if ( i == idx && i != 0 ) {
                                    $('#chatAi').append('<br>'+strArr[i]);
                                } else {
                                    $('#chatAi').append(strArr[i]+'<br>');
                                }
                            }
                        } 
                    } else if ( item.sender == 'assistant' ) {
                        let divAClone = elements.divADetach.clone();
                        divAClone.find('.chat-message').html(answer.replace(/(?:\r\n|\r|\n)/g, '<br>'));
                        $('#chatUl').append(divAClone);
                    } else {
                        let divHClone = elements.divHDetach.clone();
                        divHClone.find('.chat-message').html(answer.replace(/(?:\r\n|\r|\n)/g, '<br>'));
                        $('#chatUl').append(divHClone);
                    }
                });

                // chat 
                $('.divRight').show();
                // 전체 로딩 hide
                $('#loadingDiv').hide();
                // 로딩 삭제
                $('.divL').remove();
            } else {
                // 요약과 질문 예시
                firstAskAi();
            }

        })
        .catch(fn_handleError);
}

function updRoom(roomSeq, title, callback) {
    const url = '/api/room';

    const header = {
        'Content-Type': 'application/json',
    };

    let params = {
        "roomSeq": roomSeq,
        "title": title
    };

    fn_fetchPatchData(url, JSON.stringify(params), header)
        .then(data => {
            if ( !data ) 
                alert('ERROR');
            
            callback(data);
        })
        .catch(error => {
            fn_handleError(error);
            callback(false);
        });
}

function delRoom(roomSeq, callback) {

    const url = '/api/room/' + roomSeq;

    fn_fetchDeleteData(url)
        .then(data => {
            if ( data && roomSeq == $('#roomSeq').val() ) {
                // pdf 뷰어
                $('.divMiddle').hide();
                // chat 
                $('.divRight').hide();
                // 초기화
                $('#sourceId').val('');
                $('#roomSeq').val('');
            }

            callback(data);
        })
        .catch(error => {
            fn_handleError(error);
            callback(false);
        });
}