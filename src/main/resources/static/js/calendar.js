const bigMonth = document.getElementById('bigMonth');
const calendar = document.getElementById('calendar');
const pre_month = document.getElementById('pre_month');
const next_month = document.getElementById('next_month');
var today = new Date();
var firstDay = new Date(today.getFullYear(), today.getMonth(), 1); /* 해당 월의 첫째 날 */
var lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0); /* 해당 월의 마지막 날 */

function makeCalendar() {
	var month = today.getMonth();
	bigMonth.innerText = month + 1; /* 왼쪽 상단에 몇 월인지 표기 */

	/* calendar table 생성 */
	const newCalendar = document.createElement('table');
	newCalendar.id = 'calendar_table';
	calendar.appendChild(newCalendar);

	/* 요일 */
	const days = document.createElement('tr');
	const days_arr = new Array('(일)', '(월)', '(화)', '(수)', '(목)', '(금)', '(토)');
	for (var i = 0; i < days_arr.length; i++) {
		const day = document.createElement('td');
		day.innerText = days_arr[i];
		days.appendChild(day);
	}
	newCalendar.appendChild(days);

	/* 첫째 주 */
	var firstWeek = document.createElement('tr');
	for (var i = 0; i < firstDay.getDay(); i++) {
		firstWeek.appendChild(document.createElement('td')); /* 첫째 날이 시작하기 전까지 빈 td 채우기 */
	}
	var day = firstDay.getDate();
	for (; day <= 7 - firstDay.getDay(); day++) {
		var newDay = document.createElement('td');
		newDay.innerText = day;
		appendImgButton(newDay);
		firstWeek.appendChild(newDay);
	}

	newCalendar.appendChild(firstWeek);

	var currentWeek = firstWeek; /* 현재 작업하고 있는 tr */

	while (day <= lastDay.getDate()) {

		if (currentWeek.childElementCount == 7) { /* 현재 작업하고 있는 tr의 td가 7개라면 table에 appendChild하고 새로운 tr을 생성 */
			newCalendar.appendChild(currentWeek);
			var newWeek = document.createElement('tr');
			currentWeek = newWeek;
		}

		var newDay = document.createElement('td');
		newDay.innerText = day;
		appendImgButton(newDay);
		currentWeek.appendChild(newDay);
		day++;
	}
	newCalendar.appendChild(currentWeek);

}

function appendImgButton(newDay) {

	var yyyy = today.getFullYear();
	var MM = (today.getMonth() + 1).toString().padStart(2, '0');
	var dd = newDay.innerText.padStart(2, '0');
	var dayId = yyyy + '-' + MM + '-' + dd;

	/* var dayId = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + newDay.innerText; */

	/* popover target */
	var newPopover = document.createElement('div');
	newPopover.classList.add('popover_div');
	newPopover.popover = 'auto';
	newPopover.innerHTML = makePopoverDiv();

	/* popover button */
	var newButton = document.createElement('button');
	newButton.classList.add('date_btn')
	newButton.id = dayId;
	/*newButton.appendChild(document.createElement('img'));*/
	newButton.popoverTargetElement = newPopover;
	newButton.popoverTargetAction = 'toggle';

	newDay.appendChild(newPopover);
	newDay.appendChild(newButton);

}

function makePopoverDiv() {
	var html = '';

	html += '<div class="mci_container">';
	html += '<div class="thumbnail_zone">';
	html += '</div>'; /* thumbnail zone */
	html += '<div class="file_zone">';
	html += '<button class="img_upload_btn">+</button>';
	html += '</div>'; /* file zone */
	html += '</div>'; /* mci_container */

	return html;
}

function loadMonthlyReport() {
	/* 최근 한 달 간 업로드한 report의 thumbnail을 선택 할 수 있도록 */
	/* popover의 file zone에 thumbnail 나열 */
	const year = firstDay.getFullYear();
	const month = (firstDay.getMonth() + 1).toString().padStart(2, '0');
	const fday = firstDay.getDate().toString().padStart(2, '0');
	const lday = lastDay.getDate().toString().padStart(2, '0');

	$.ajax({
		url: '/report/loadMonthlyReport',
		data: {
			'firstDayString': year + '-' + month + '-' + fday,
			'lastDayString': year + '-' + month + '-' + lday
		},
		dataType: 'json',
		success: function(list) {
			console.log(list);

			if (!list || list.length == 0) {
				console.log('no exist report');
			} else {
				/*var imgs = '';
				list.forEach(src => {
					imgs += '<img src="' + src + '">';
				});*/


				/*$('.file_zone').each(function() {
					$(this).append(imgs);
				})*/

				var file_zone = document.querySelectorAll('.file_zone');
				file_zone.forEach((zone) => {
					/*list.forEach((src) => {
						var newImg = document.createElement('img');
						newImg.src = src;
						addDragEvent(newImg);
						zone.appendChild(newImg);
					})*/

					list.forEach((report) => {
						var newImg = document.createElement('img');
						/*newImg.src = report.thumbnail;*/
						if (report.thumbnail.includes('http', 0)) {
							newImg.src = report.thumbnail;
						} else {
							newImg.src = '/img/' + report.thumbnail;
						}
						addDragEvent(newImg);
						zone.appendChild(newImg);

					})
				});

			}
		}
	});
}

function addDragEvent(newImg) {
	/*newImg.addEventListener('click', function() {
		console.log(newImg.src);
	});*/
	newImg.addEventListener('dragstart', function(e) {
		console.log(e.target.src);
		e.dataTransfer.setData("text", e.target.src); /* drag 시작한 img의 src 저장 */
	});
}

function loadMonthlyCalendar() {
	var yyyy = today.getFullYear();
	var MM = (today.getMonth() + 1).toString().padStart(2, '0');

	for (var i = firstDay.getDate(); i <= lastDay.getDate(); i++) {
		var dd = i.toString().padStart(2, '0');
		var date = yyyy + '-' + MM + '-' + dd;
		loadDate(date);
	}

}

function loadDate(date) {

	/*console.log(date);*/

	$.ajax({
		url: '/calendar/loadDate',
		data: {
			'readdateString': date
		},
		type: 'POST',
		dataType: 'json',
		success: function(list) {
			/*console.log('loadDate',list);*/
			if (!list || list.length == 0) {
			} else {
				var date_btn = document.getElementById(date);
				makeDateThumbnail(list, date_btn);
				appendThumbnails(list, date_btn);
			}
		}
	});
}

function makeDateThumbnail(list, date_btn) {

	var newCollage = '';
	var num = 0;

	if (date_btn.childElementCount > 0) {
		if (date_btn.firstElementChild.classList.contains('collage_container')) {
			newCollage = date_btn.firstElementChild;
			var lastChildClass = newCollage.lastElementChild.className;
			console.log(lastChildClass);
			num = parseInt(lastChildClass.replace(/[^0-9]/g, '')) + 1;
			console.log(num);
		}
	} else {
		newCollage = document.createElement('div');
		newCollage.classList.add('collage_container');
	}

	list.forEach((data) => {
		var newThumbnail = document.createElement('img');
		newThumbnail.src = data.thumbnail;
		if (num > 5) {
			num = 2;
		}
		newThumbnail.classList.add('img' + num);
		num++;

		newCollage.appendChild(newThumbnail);
	});

	date_btn.appendChild(newCollage);
}

function appendThumbnails(list, date_btn) {
	var thumbnail_zone = date_btn.previousElementSibling.firstElementChild.firstElementChild;

	list.forEach((data) => {
		var newThumbnailDiv = document.createElement('div');
		newThumbnailDiv.classList.add('thumbnail_img_wrapper');
		var newThumbnail = document.createElement('img');
		newThumbnail.src = data.thumbnail;
		newThumbnailDiv.appendChild(newThumbnail);
		thumbnail_zone.appendChild(newThumbnailDiv);
		var delete_btn = document.createElement('button');
		delete_btn.innerText = 'x';
		newThumbnailDiv.appendChild(delete_btn);
		delete_btn.addEventListener('click', function() {
			/*console.log(data.calendarid);*/
			$.ajax({
				url: 'calendar/deleteCalendar',
				data: {
					'calendarid': data.calendarid,
				},
				type: 'POST',
				success: function(response) {
					console.log(response);
					newThumbnailDiv.remove();
				}
			}); /* ajax */
		});
		/*var newThumbnail = document.createElement('img');
		newThumbnail.src = data.thumbnail;

		thumbnail_zone.appendChild(newThumbnail);*/
	});
}

function addCalendarEvent() {
	/* new thumbnail upload */
	const img_upload_btns = document.querySelectorAll('.img_upload_btn');

	img_upload_btns.forEach((btn) => {
		btn.addEventListener('click', function() {

			/* file_zone 내에 이미 생성된 input이 있는지 확인 */
			var file_zone = btn.parentElement;
			var children = file_zone.children; /* file_zone의 children 요소들 */
			var hasInput = false;
			for (var i = 0; i < children.length; i++) {
				if (children[i].tagName == 'INPUT') {
					hasInput = true;
					children[i].click();
					break;
				}
			}

			if (!hasInput) { /* input 생성과 addEventListener */
				var dayIdElement = btn.parentElement.parentElement.parentElement.nextElementSibling;
				var dayId = dayIdElement.id;

				var newInput = document.createElement('input');
				newInput.type = 'file';

				newInput.addEventListener('change', function() {
					var formData = new FormData();
					var inputFile = newInput;
					var thumbnail = inputFile.files[0];

					formData.append("thumbnail", thumbnail);
					formData.append("readdate", dayId);

					$.ajax({
						url: '/calendar/createCalendar',
						processData: false,
						contentType: false,
						data: formData,
						type: 'POST',
						dataType: 'json',
						success: function(list) {
							console.log(list);
							/*loadDate(dayId);*/
							var date_btn = dayIdElement;
							makeDateThumbnail(list, date_btn);
							appendThumbnails(list, date_btn);
						}
					}); // ajax
				})
				file_zone.appendChild(newInput);
				newInput.click();
			}

		});
	}); /* img upload btn click event */

	const thumbnail_zone = document.querySelectorAll('.thumbnail_zone');
	thumbnail_zone.forEach((zone) => {

		zone.addEventListener('dragover', (e) => {
			e.preventDefault();
		});

		zone.addEventListener('drop', (e) => {
			e.preventDefault();

			const imgUrl = e.dataTransfer.getData('text/plain');
			/*console.log('drop', imgUrl);*/
			var dayIdElement = zone.parentElement.parentElement.nextElementSibling;
			var dayId = dayIdElement.id;

			var formData = new FormData();

			formData.append("imgUrl", imgUrl);
			formData.append("readdate", dayId);

			$.ajax({
				url: '/calendar/createCalendar',
				processData: false,
				contentType: false,
				data: formData,
				type: 'POST',
				dataType: 'json',
				success: function(list) {
					console.log(list);
					/*loadDate(dayId);*/
					var date_btn = dayIdElement;
					makeDateThumbnail(list, date_btn);
					appendThumbnails(list, date_btn);
				}
			}); // ajax
		});
	})
}

document.addEventListener("DOMContentLoaded", function() {
	makeCalendar();
	loadMonthlyReport();
	addCalendarEvent();
	loadMonthlyCalendar();
});

pre_month.addEventListener("click", function() {
	const thisCalendar = document.getElementById('calendar_table');
	thisCalendar.remove();

	var this_year = today.getFullYear();
	var this_month = today.getMonth();

	var newMonth = '';

	if (this_month == 1) { /* 저번 달이 작년 12월일 경우 */
		newMonth = new Date(this_year - 1, 12, 1);
	} else {
		newMonth = new Date(this_year, this_month - 1, 1);
	}

	today = newMonth;
	firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
	lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

	makeCalendar();
	addCalendarEvent();
	loadMonthlyReport();
	loadMonthlyCalendar();
})

next_month.addEventListener("click", function() {
	const thisCalendar = document.getElementById('calendar_table');
	thisCalendar.remove();

	var this_year = today.getFullYear();
	var this_month = today.getMonth();

	var newMonth = '';

	if (this_month == 12) { /* 다음 달이 내년 1월일 경우 */
		newMonth = new Date(this_year + 1, 1, 1);
	} else {
		newMonth = new Date(this_year, this_month + 1, 1);
	}

	today = newMonth;
	firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
	lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

	makeCalendar();
	addCalendarEvent();
	loadMonthlyReport();
	loadMonthlyCalendar();
});
