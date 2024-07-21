const report_list = document.getElementById('report_list');

console.log(list);
var reports = list.content;
var i = 0;
for (; i < reports.length; i++) {
	const newReportLi = document.createElement('li');
	report_list.appendChild(newReportLi);
	const newReportDiv = document.createElement('div');
	newReportDiv.classList.add('report_thumbnail');
	newReportLi.appendChild(newReportDiv);
	const newReport = document.createElement('a');
	newReport.href = '/report/read/' + reports[i].reportid;
	newReportDiv.appendChild(newReport);
	const newReportImg = document.createElement('img');
	if (reports[i].thumbnail.includes('http', 0)) {
		newReportImg.src = reports[i].thumbnail;
	} else {
		newReportImg.src = '/img/' + reports[i].thumbnail;
	}
	newReport.appendChild(newReportImg);

	/* const newShelf = document.createElement('div');
	newShelf.classList.add('shelves');
	newReportLi.appendChild(newShelf); */

	const newShelf = document.createElement('hr');
	newReportLi.appendChild(newShelf);
}

for (; i < 20; i++) {
	const newReportLi = document.createElement('li');
	report_list.appendChild(newReportLi);
	const newReportDiv = document.createElement('div');
	newReportDiv.classList.add('report_thumbnail');
	newReportLi.appendChild(newReportDiv);
	const newReportImg = document.createElement('img');
	newReportDiv.appendChild(newReportImg);
	const newShelf = document.createElement('hr');
	newReportLi.appendChild(newShelf);
}

const pageList = document.getElementById('page_list');
for (var p = startPage; p <= endPage; p++) {
	const newPageLi = document.createElement('li');
	pageList.appendChild(newPageLi);
	const newPage = document.createElement('a');
	newPage.innerText = p;
	newPage.href = '/bookshelf/' + p;
	newPageLi.appendChild(newPage);
	if (p == nowPage) {
		newPage.classList.add('nowPage');
	}
}