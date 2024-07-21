var today = new Date();
var firstDay = new Date(today.getFullYear(), today.getMonth(), 1); /* 해당 월의 첫째 날 */
var lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0); /* 해당 월의 마지막 날 */

function loadMonthlyReport() {
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
			
			const totalSpan = document.getElementById('total');
			const countSpan = document.getElementById('count');
			
			const bookList = document.getElementById('book_list');				
			const newList = document.createElement('ol');

			if (!list || list.length == 0) {
				console.log('no exist report');
				totalSpan.innerText = '0';
				countSpan.innerText = '0';
			} else {
				var total = 0;
				
				bookList.appendChild(newList);
				
				list.forEach((report) => {
					const newLi = document.createElement('li');
					newList.appendChild(newLi);
					newLi.innerText = report.title;
					
					const newPage = document.createElement('span');
					newLi.appendChild(newPage);
					newPage.classList.add('right_span');
					newPage.innerText = report.page;
					
					total += report.page;
				});
				
				totalSpan.innerText = total;
				countSpan.innerText = list.length;
			}
		}
	});
}

function setDate() {
	const year = today.getFullYear();
	const month = (today.getMonth() + 1).toString().padStart(2, '0');
	const fday = firstDay.getDate().toString().padStart(2, '0');
	const day = today.getDate().toString().padStart(2, '0');
	
	const firstDayString = year + '.' + month + '.' + fday;
	const todayString = year + '.' + month + '.' + day;
	
	const thisMonth = document.getElementById('this_month');
	thisMonth.innerText = firstDayString + ' - ' + todayString;
	
	const date = document.getElementById('date');
	date.innerText = todayString;
}

document.addEventListener('DOMContentLoaded', () => {
	setDate();
	loadMonthlyReport();
});