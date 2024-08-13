document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('cancel-subscription-btn').addEventListener('click', function () {
        fetch('/subscription/cancel-subscription', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('サブスクリプションが解約されました。');
                // 必要に応じてページをリロードするか、他の処理を行う
            } else {
                alert('サブスクリプションの解約に失敗しました: ' + data.error);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });
});