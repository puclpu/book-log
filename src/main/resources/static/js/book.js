
/* rating star */
const rating_input = document.getElementById('rating_input');
const rating_star = document.getElementById('rating_star2');

rating_input.addEventListener('input', function() {
	var value = rating_input.value;
	console.log('value');
	var width = (value / 10) * 100;
	console.log('width');
	rating_star.style.width = width + '%';
});



/* editor */
const editor = document.getElementById('editor');
const quote_btn = document.getElementById('quote_btn');
const pic_btn = document.getElementById('pic_btn');
const qna_btn = document.getElementById('qna_btn');
const tree_btn = document.getElementById('tree_btn');
const pic_input = document.getElementById('pic_input');
const ques_btn = document.querySelectorAll('.ques_btn');

let lastFocusBlock = null;

/* block */
function block() {
	const newBlock = document.createElement('div');
	newBlock.setAttribute('contenteditable', 'true');
	newBlock.classList.add('block');
	newBlock.focus(); /* 생성된 block에 focus */

	newBlock.addEventListener('keydown', function(e) {
		if (e.keyCode == 13) { /* enter event */
			e.preventDefault(); /* 기본 설정 작업을 막음 */

			var content = newBlock.innerText;

			if (content == '') { /* input에 입력된 값이 없을 경우 아래에 새 block 생성 */
				const nextNewBlock = block();
				newBlock.insertAdjacentElement('afterend', nextNewBlock);
				nextNewBlock.focus();
			} else { /* 커서 이후의 문자열을 아래에 새 block을 생성한 후 옮김 */
				const selection = document.getSelection();
				const range = selection.getRangeAt(0);

				var beforeContext = content.substring(0, range.startOffset);
				var afterContext = content.substring(range.endOffset);

				const nextNewBlock = block();
				newBlock.insertAdjacentElement('afterend', nextNewBlock);
				nextNewBlock.focus();
				newBlock.innerText = beforeContext;
				nextNewBlock.innerText = afterContext;
			}

		} else if (e.keyCode == 8 && newBlock.innerText == '' && editor.childElementCount > 0) { /* backspace event */
			var focusBlock = focusPreviousBlock(newBlock);
			if (focusBlock.classList.contains('pic')) { // 삭제할 block이 img라면 bucket에서도 삭제
				var imgUrl = focusBlock.src;
				imgDelete(imgUrl);
			}
			focusBlock.remove(); /*	focus한 block 삭제 */

		} else if (e.keyCode == 38) { /* arrow up event */
			var focusBlock = focusPreviousBlock(newBlock);

		} else if (e.keyCode == 40) { /* arrow down event */
			focusNextBlock(newBlock);
		} else {
			return;
		}

	}); /* keydown event */

	newBlock.addEventListener('focus', function() {
		lastFocusBlock = newBlock;
	});

	return newBlock;
}

function imgDelete(imgUrl) {
	$.ajax({
		url: '/report/imgdelete',
		data: {
			"imgUrl": imgUrl
		},
		type: 'POST',
		success: function() {
			console.log('img 삭제');
		}
	})
}

function focusPreviousBlock(newBlock) {
	const parent = newBlock.parentElement; /* 현재 block의 parent element */
	var focusBlock = newBlock;
	if (newBlock.previousSibling) { /* 현재 block 전에 sibling element가 존재할 때 */
		if (newBlock.previousSibling.classList.contains('pic')) { /* previous sibling element가 img라면 (newBlock == img 바로 다음 block) */
			focusBlock = newBlock.previousSibling; /* focusBlock은 img */
		} else if (parent.classList.contains('qna')) { /* 현재 block의 parent element가 qna element라면 (newBlock == qna의 첫번째 child) */
			focusBlock = parent; /* focusBlock은 qna element */
		} else {
			focusBlock = newBlock;
		}
	} else if (parent.classList.contains('quote')) { /* previous sibling이 없고, parent element가 quote element라면 (newBlock == quote의 첫번째 child) */
		focusBlock = parent; /* 삭제할 element는 현재 block의 부모 element */
	}

	if (focusBlock.previousSibling) {
		console.log(focusBlock.classList);
		if (focusBlock.previousSibling.classList.contains('pic')) { /* previousBlock이 img라면 img 뒤에 새 block 생성 */
			focusBlock.previousSibling.insertAdjacentElement('afterend', block());
		} else if (focusBlock.previousSibling.classList.contains('quote') || focusBlock.previousSibling.classList.contains('qna')) { /* previousBlock이 quote 또는 qna라면 */
			focusBlock.previousSibling.lastElementChild.focus(); /* 해당 element 내의 마지막 block을 focus */
		} else {
			focusBlock.previousSibling.focus(); /* 이전 element를 focus */
		}
	}

	return focusBlock;
}

function focusNextBlock(newBlock) {
	const parent = newBlock.parentElement;
	var focusBlock = newBlock;
	if (!newBlock.nextSibling && (parent.classList.contains('quote') || parent.classList.contains('qna'))) { /* next sibling element가 없을 때, 해당 block의 parent element가 quote 또는 qna라면 (newBlock == qna 또는 quote의 마지막 child) */
		focusBlock = parent;
	}

	if (focusBlock.nextSibling) {
		if (focusBlock.nextSibling.classList.contains('pic')) { /* next sibling element가 img일 때 */
			if (focusBlock.nextSibling.nextSibling && focusBlock.nextSibling.nextSibling.classList.contains('block')) { /* img 다음에 block이 존재하면 block을 focus */
				focusBlock.nextSibling.nextSibling.focus();
			} else { /* img 다음에 block이 없으면 block 추가 */
				focusBlock.nextSibling.insertAdjacentElement('afterend', block());
			}
		} else if (focusBlock.nextSibling.classList.contains('quote' || focusBlock.nextSibling.classList.contains('qna'))) { /* next element가 quote 또는 qna element라면 해당 element의 마지막 child를 focus */
			focusBlock.nextSibling.lastElementChild.focus();
		} else {
			focusBlock.nextSibling.focus();
		}
	}
}

document.addEventListener("DOMContentLoaded", function() {
	const newBlock = block();
	editor.appendChild(newBlock);
	lastFocusBlock = newBlock;
});

editor.addEventListener('click', function() {
	if (editor.childElementCount == 0 || !editor.lastElementChild.classList.contains('block')) { /* editor에 element가 없거나 lastElementChild가 block이 아니라면 block 추가 */
		const newBlock = block();
		editor.appendChild(newBlock);
		lastFocusBlock = newBlock;
	}
});

/* quote btn click */
quote_btn.addEventListener('click', function() {
	const quoteBlock = document.createElement('blockquote');
	quoteBlock.classList.add('quote');
	const newBlock = block();
	quoteBlock.appendChild(newBlock);
	lastFocusBlock.insertAdjacentElement('beforebegin', quoteBlock);
	newBlock.focus();
});

/* qna btn click */
qna_btn.addEventListener('click', function() { /* question list show and hide */
	const qna_list = document.getElementById('qna_list');
	if (qna_list.style.display === 'none') {
		qna_list.style.display = 'block';
	} else {
		qna_list.style.display = 'none';
	}
});

ques_btn.forEach(btn => { /* new question block */
	btn.addEventListener('click', function() {

		const qnaBlock = document.createElement('div');
		qnaBlock.classList.add('qna');
		qnaBlock.classList.add('ques_div');
		const qDiv = document.createElement('div');
		qDiv.classList.add('q_div');
		qnaBlock.appendChild(qDiv);
		const qSpan = document.createElement('span');
		qSpan.innerHTML = this.innerText;
		qDiv.appendChild(qSpan);
		const newBlock = block();
		qnaBlock.appendChild(newBlock);
		lastFocusBlock.insertAdjacentElement('beforebegin', qnaBlock);
		newBlock.focus();
	});
});

/* pic btn click */
pic_btn.addEventListener('click', function() {
	const newBlock = document.createElement('div');
	pic_input.click();
});
pic_input.addEventListener('change', function(e) {
	var formData = new FormData();
	var inputFile = document.querySelector('input[name="uploadFile"]');
	var files = inputFile.files;
	/* console.log(files); */

	for (var i = 0; i < files.length; i++) {
		formData.append("uploadFile", files[i]);
	}

	$.ajax({
		url: '/report/imgupload',
		processData: false,
		contentType: false,
		data: formData,
		type: 'POST',
		dataType: 'json',
		success: function(imgArr) {
			console.log(imgArr);

			if (!imgArr || imgArr.length == 0) {
				alert('사진 업로드에 실패했습니다.');
			} else {
				imgArr.forEach(function(obj) {
					const newPic = picture(obj);
					lastFocusBlock.insertAdjacentElement('beforebegin', newPic);
				});
			}
		}
	}); // ajax
});

function picture(path) {
	const newPic = document.createElement('img');
	newPic.src = path; /* img 경로 설정 */
	newPic.classList.add('pic');

	return newPic;
}

function submitForm() {
	const editorContent = editor.innerHTML;

	const hiddenContent = document.createElement('input');
	hiddenContent.type = 'hidden';
	hiddenContent.name = 'content';
	hiddenContent.value = editorContent;

	const form = document.getElementById('report_form');
	form.appendChild(hiddenContent);
}

function check() {
	const title = document.getElementByName('title')[0];
	const author = document.getElementByName('author')[0];
	const thumbnail = document.getElementByName('thumbnail')[0];

	if (title.value == '' || author.value == '' || thumbnail.value == '') {
		return false;
	} else {
		return true;
	}
}

