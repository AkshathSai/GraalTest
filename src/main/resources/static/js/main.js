const form = document.querySelector('#video-form');
const videoDiv = document.querySelector('#video-player');
const videoScreen = document.querySelector('#video-screen');
const fileInput = document.querySelector('#file');
let fileName;
const queryParams = Object.fromEntries(new URLSearchParams(window.location.search));

fetch('http://localhost:8080/music/titles')
    .then(result => result.json())
    .then(result => {

        const videos = document.querySelector('#your-videos');
        if (result.length > 0) {
            for (let vid of result) {
                const li = document.createElement('LI');
                const link = document.createElement('A');
                link.innerText = vid;
                link.href = window.location.origin + window.location.pathname + '?video=' + encodeURIComponent(vid);
                li.appendChild(link);

                const deleteButton = document.createElement('BUTTON');
                //deleteButton.innerText = 'Delete';
                deleteButton.className = 'btn btn-default btn-xs';
                deleteButton.setAttribute("style", "margin-left: 10px;");

                const binIcon = document.createElement('SPAN');
                binIcon.className = 'glyphicon glyphicon-trash';
                deleteButton.appendChild(binIcon);

                deleteButton.onclick = function() {
                    deleteVideo(vid);
                }
                li.appendChild(deleteButton);

                videos.appendChild(li);
            }
        } else {
            videos.innerHTML = 'Nothing has been found';
        }

    });

if (queryParams.video) {
    videoScreen.src = `http://localhost:8080/music/${decodeURIComponent(queryParams.video)}`;
    videoDiv.style.display = 'block';
    document.querySelector('#now-playing')
        .innerText = 'Now playing ' + decodeURIComponent(queryParams.video);
}


fileInput.addEventListener('change',ev=>{
    fileName=ev.target.files[0].name;

    ev.preventDefault();
    let data = new FormData(form);
    data.set("name",fileName)
    fetch('http://localhost:8080/music', {
        method: 'POST',
        body: data
    }).then(result => result.text()).then(_ => {
        window.location.reload();
    });
});

function deleteVideo(name) {
    fetch(`http://localhost:8080/music/${encodeURIComponent(name)}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.status === 204) {
                console.log(`Video ${name} deleted successfully.`);
                // you might want to remove the video from the UI here
                window.location.reload();
            } else {
                console.error(`Failed to delete video ${name}, status code = ${response.status}`);
            }
        })
        .catch(error => console.error(`Failed to delete video ${name}: ${error}`));
}
