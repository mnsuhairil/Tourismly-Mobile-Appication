<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tourism Admin Panel - Login</title>
    
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="styles.css">
    <style>
        body {
            background-image: url('https://wallpaperboat.com/wp-content/uploads/2019/10/free-website-background-21.jpg'); /* Replace with your image path or URL */
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
    </style>
</head>
<body>

<div class="col-md-4">
    <div class="card">
        <!-- Add your company image here with a fixed size -->
        <div class="card-body text-center p-4">
        <img src="itourismly-image.png" alt="Company Logo" width="150" height="150" >
            <h2 class="mb-4">Admin Login</h2>
            <div class="mb-3">
                <input type="text" id="username" class="form-control" placeholder="Username">
            </div>
            <div class="mb-3">
                <input type="password" id="password" class="form-control" placeholder="Password">
            </div>
            <button id="loginBtn" class="btn btn-primary btn-block">Login</button>
        </div>
    </div>
</div>


    <!-- Bootstrap JS and jQuery -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Include Firebase SDK and your app.js -->
    <script type="module" src="https://www.gstatic.com/firebasejs/9.6.0/firebase-app.js"></script>
    <script type="module" src="https://www.gstatic.com/firebasejs/9.6.0/firebase-auth.js"></script>
    <script type="module" src="https://www.gstatic.com/firebasejs/9.6.0/firebase-database.js"></script>
    <script type="module" src="https://www.gstatic.com/firebasejs/9.6.0/firebase-storage.js"></script>
    <script type="module" src="app.js"></script>
</body>
</html>
