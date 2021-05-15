/*global THREE, requestAnimationFrame, console*/
/*  Carolina Ramos ist193694
    Beatriz Venceslau ist193734
    Diogo Cabral ist193704*/

//global variables
//scene essentials
var cameraAtiva, cameraTopo, cameraPerspetivaMovel, cameraPerspetiva
var scene, renderer;
var geometry, material, mesh, axes;

//taco variables
var taco, ang1, ang2;
var pivotGroup = new Array(7);
var tacoGroup = new Array(7);
var selectedTaco = 0, lastSelectedTaco = 0, i = 1;

//ball variables
var ball;
var pivotBall = new Array(22);
var ballGroup = new Array(22);
var selectedBall = 0, n = 1;

//hole variables
var holeGroup = new Array(7);

//motion variables
var left, right; //taco direction
var velocity = 0.005, clock, delta; //ball movement
var infiniteBall; //hole treatment

//flags
var changeColor = false;
var tacada = false, infinite = false;

//functions
//object creation
function addTableLeg(obj, x, y, z) {
    'use strict';

    geometry = new THREE.CubeGeometry(2, 10, 2);
    mesh = new THREE.Mesh(geometry, material);
    mesh.position.set(x, y - 5, z);
    obj.add(mesh);
}

function addTableTop(obj, x, y, z) {
    'use strict';
    geometry = new THREE.CubeGeometry(100, 1, 60);
    mesh = new THREE.Mesh(geometry, material);
    mesh.position.set(x, y, z);
    obj.add(mesh);
}

function addTableTopWall(obj, x, y, z) {
    'use strict';
    geometry = new THREE.CubeGeometry(100, 3, 2);
    mesh = new THREE.Mesh(geometry, material);
    mesh.position.set(x, y+1, z);
    obj.add(mesh);
}

function addTableTopWall2(obj, x, y, z) {
    'use strict';
    geometry = new THREE.CubeGeometry(2, 3, 60-4);
    mesh = new THREE.Mesh(geometry, material);
    mesh.position.set(x, y+1, z);
    obj.add(mesh);
}

function createPivot(x,y,z) {
    pivotGroup[i] = new THREE.Object3D();
    pivotGroup[i].position.set(x, y, z);
    pivotGroup[i].add(tacoGroup[i]);
    scene.add(pivotGroup[i]);
    i = i + 1;
}

function createTaco(x, y, z, ang1, ang2) {
    'use strict';

    tacoGroup[i] = new THREE.Object3D();
    tacoGroup[i].userData = { number:i, ang: 0, selected: false, deuTacada: 0};

    material = new THREE.MeshBasicMaterial({ color: 0xf9cb9c, wireframe: true });
    geometry = new THREE.CylinderGeometry(0.15, 0.5, 40);
    mesh = new THREE.Mesh(geometry, material);
    tacoGroup[i].add(mesh);

    tacoGroup[i].position.set(x, y, z);
    tacoGroup[i].rotateZ(ang1);
    tacoGroup[i].rotateX(ang2);
    scene.add(tacoGroup[i]);
    i = i + 1;
}

function createBall(x, y, z, n, ang) {
    'use strict';

    var vInit = THREE.Math.randFloat(0.9, 1.5);

    ballGroup[n] = new THREE.Object3D();
    if(n>6) {
      ballGroup[n].userData = { ang: ang, speedinit: vInit, angOnAxis:0, moving: true, playable: true};
      material = new THREE.MeshBasicMaterial({ color: 0xff0000, wireframe: true });
    }
    else {
      ballGroup[n].userData = { ang: ang, speedinit: vInit, angOnAxis:0, moving: false, playable: false};
      material = new THREE.MeshBasicMaterial({ color: 0xFAEBD7, wireframe: true });
    }

    geometry = new THREE.SphereGeometry(1.5, 60, 10);
    mesh = new THREE.Mesh(geometry, material);
    ballGroup[n].add(mesh);

    ballGroup[n].position.set(0, 0, 0);
    ballGroup[n].rotateY(Math.PI/2);
    axes = new THREE.AxisHelper(10);

    ballGroup[n].add(axes);
    scene.add(ballGroup[n]);

    pivotBall[n] = new THREE.Object3D();
    pivotBall[n].position.set(x, y, z);
    pivotBall[n].rotateY(-Math.PI/4);
    pivotBall[n].rotateY(ang);
    pivotBall[n].add(ballGroup[n]);
    scene.add(pivotBall[n]);
}

function createHole(x,y,z){
  holeGroup[i] = new THREE.Object3D();
  geometry = new THREE.CylinderGeometry(3, 3, 0.5, 60);
  material = new THREE.MeshBasicMaterial({ color: 0x000000, wireframe: true });
  mesh = new THREE.Mesh(geometry, material);
  holeGroup[i].add(mesh);

  holeGroup[i].position.set(x,y,z);0
  scene.add(holeGroup[i]);
  i = i+1;
}


function createTable(x, y, z) {
    'use strict';

    var table = new THREE.Object3D();

    material = new THREE.MeshBasicMaterial({ color: 0x008200, wireframe: false });

    //Table Top dimensions - 100 x 60
    addTableTop(table, 0, 0, 0);

    material = new THREE.MeshBasicMaterial({ color: 0x3e2002, wireframe: false });

    //Table Top dimensions after adding walls - 98 x 38
    addTableTopWall(table, 0, 1, 29);
    addTableTopWall(table, 0, 1, -29);
    addTableTopWall2(table, 49, 1, 0);
    addTableTopWall2(table, -49, 1, 0);

    addTableLeg(table, -45, -1, -28);
    addTableLeg(table, -45, -1, 28);
    addTableLeg(table, 45, -1, 28);
    addTableLeg(table, 45 , -1, -28);

    scene.add(table);

    table.position.set(x, y, z);
}

function createScene() {
    'use strict';

    scene = new THREE.Scene();

    createTable(0, 4, 0);

    createTaco(0, 0, -21, Math.PI/2, Math.PI/2);
    createTaco(0, 0, -21, Math.PI/2, Math.PI/2);
    createTaco(21, 0, 0, Math.PI/2, 0);
    createTaco(0, 0, 21, 0, -Math.PI/2);
    createTaco(0, 0, 21,0, -Math.PI/2);
    createTaco(-21, 0, 0, Math.PI/2, Math.PI);

    //the end of each Taco
    i= 1;
    createPivot(-25, 6, -30.5);
    createPivot(25, 6, -30.5);
    createPivot(50.5, 6, 0);
    createPivot(25, 6, 30.5);
    createPivot(-25, 6, 30.5);
    createPivot(-50.5, 6, 0);

    //create balls at the end of Taco
    createBall(-25, 6, -25, 1, 0);
    createBall(25, 6, -25, 2, 0);
    createBall(45, 6, 0, 3, -Math.PI/2);
    createBall(25, 6, 25, 4, -Math.PI);
    createBall(-25, 6, 25, 5, -Math.PI);
    createBall(-45, 6, 0, 6, Math.PI/2);

    //create 15 random balls
    //each group is a column
    createBall(THREE.Math.randFloat(-27, -21), 6, THREE.Math.randFloat(-15, -9), 7, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(-27, -21), 6, THREE.Math.randFloat(-3, 3), 8, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(-27, -21), 6, THREE.Math.randFloat(9, 15), 9, THREE.Math.randFloat(0, 2*Math.PI));

    createBall(THREE.Math.randFloat(-15, -9), 6, THREE.Math.randFloat(-15, -9), 10, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(-15, -9), 6, THREE.Math.randFloat(-3, 3), 11, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(-15, -9), 6, THREE.Math.randFloat(9, 15), 12, THREE.Math.randFloat(0, 2*Math.PI));

    createBall(THREE.Math.randFloat(-3, 3), 6, THREE.Math.randFloat(-15, -9), 13, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(-3, 3), 6, THREE.Math.randFloat(-3, 3), 14, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(-3, 3), 6, THREE.Math.randFloat(9, 15), 15, THREE.Math.randFloat(0, 2*Math.PI));

    createBall(THREE.Math.randFloat(9, 15), 6, THREE.Math.randFloat(-15, -9), 16, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(9, 15), 6, THREE.Math.randFloat(-3, 2), 17, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(9, 15), 6, THREE.Math.randFloat(9, 15), 18, THREE.Math.randFloat(0, 2*Math.PI));

    createBall(THREE.Math.randFloat(21, 27), 6, THREE.Math.randFloat(-15, -9), 19, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(21, 27), 6, THREE.Math.randFloat(-3, 2), 20, THREE.Math.randFloat(0, 2*Math.PI));
    createBall(THREE.Math.randFloat(21, 27), 6, THREE.Math.randFloat(9, 15), 21, THREE.Math.randFloat(0, 2*Math.PI));


    //create 6 holes
    i = 1;
    createHole(-45,4.5,-25);
    createHole(0,4.5,-25);
    createHole(45,4.5,-25);
    createHole(45,4.5,25);
    createHole(0,4.5,25);
    createHole(-45,4.5,25);

}

//create cameras
function createCamera() {
  'use strict';

  createCamTopo();
  createCamPresp();
  createCamPrespMovel()

  //camaraAtiva = variavel que indica qual a camera ativa no momento
  //inicia com visao frontal
  cameraAtiva = cameraPerspetiva;
}

function createCamTopo() {
  cameraTopo = new THREE.OrthographicCamera (200 / - 2, 200 / 2, 200 / 2, 200 / - 2, 1, 1000 );
  cameraTopo.position.x = 0;
  cameraTopo.position.y = 200;
  cameraTopo.position.z = 0;
  cameraTopo.lookAt (new THREE.Vector3(0, 1, 0));
}

function createCamPresp() {
  cameraPerspetiva =new THREE.PerspectiveCamera(70,
                                                window.innerWidth / window.innerHeight,
                                                1,
                                                1000);
  cameraPerspetiva.position.x = 70;
  cameraPerspetiva.position.y = 70;
  cameraPerspetiva.position.z = 70;
  cameraPerspetiva.lookAt(new THREE.Vector3(0, 0, 0));
}

function createCamPrespMovel() {
  cameraPerspetivaMovel =new THREE.PerspectiveCamera(70,
                                                window.innerWidth / window.innerHeight,
                                                1,
                                                1000);
  cameraPerspetivaMovel.position.x = -30;
  cameraPerspetivaMovel.position.y = 30;
  cameraPerspetivaMovel.position.z = -30;
  cameraPerspetivaMovel.lookAt(new THREE.Vector3(0, 0, 0));
}

function onKeyDown(e) {
    'use strict';

    switch (e.keyCode) {
        case 37: //arrow left
            left = true;
            break;
        case 39: //arrow right
            right = true;
            break;
        case 52: //4
            callTaco(1);
            break;
        case 53: //5
            callTaco(2);
            break;
        case 54: //6
            callTaco(3);
            break;
        case 55: //7
            callTaco(4);
            break;
        case 56: //8
            callTaco(5);
            break;
        case 57: //9
            callTaco(6);
            break;
        case 32: //space
            tacada = true;
            break;
        case 49: //1
            //muda camera selecionada para visao topo
           cameraAtiva = cameraTopo;
           break;
        case 50: //2
            //muda camera selecionada para perspetiva
            cameraAtiva = cameraPerspetiva;
            break;
        case 51: //3
            //muda camera selecionada para perspetiva movel da bola
            if(selectedBall != 0){
              pivotBall[selectedBall].add(cameraPerspetivaMovel);
              cameraAtiva = cameraPerspetivaMovel;
            }
            else
              cameraAtiva = cameraPerspetiva;
             break;
    }
}

function onKeyUp(e) {
    'use strict';

    switch (e.keyCode) {
      case 37: // arrow left
        left = false;
        break;
      case 39: // arrow right
        right = false;
        break;

    }
}

function render() {
    'use strict';
      renderer.render (scene,cameraAtiva);
}

function init() {
    'use strict';
    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    clock = new THREE.Clock();
    createScene();
    createCamera();
    animate();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
}

function callTaco (number) {
  if(selectedTaco == 0) {
    changeColor= true;
    selectedTaco = number;
    selectedBall = number;
    if(tacoGroup[selectedTaco].userData.deuTacada = 1) {
      checkBallPlace(selectedBall);
    }
  }
  else if(selectedTaco == number){
    changeColor= true;
    selectedBall = 0;
  }
}

function mudaCorTaco() {
  if(changeColor){  //flag that allows to change colors
    if(tacoGroup[selectedTaco].userData.selected == false) {
      //if it was not selected (not red)
      material = new THREE.MeshBasicMaterial({ color: 0xFF0000, wireframe: true });
      geometry = new THREE.CylinderGeometry(0.15, 0.5, 40);
      mesh = new THREE.Mesh(geometry, material);
      tacoGroup[selectedTaco].add(mesh);
      tacoGroup[selectedTaco].userData.selected = true; //selects taco
      changeColor = false;
    }
    else {
      //if it was selected (red)
      material = new THREE.MeshBasicMaterial({ color: 0xf9cb9c, wireframe: true});
      tacoGroup[selectedTaco].userData.selected = false;
      geometry = new THREE.CylinderGeometry(0.15, 0.5, 40);
      mesh = new THREE.Mesh(geometry, material);
      if(selectedTaco!= 0) {
        ang = tacoGroup[selectedTaco].userData.ang;
        pivotGroup[selectedTaco].rotateY(-ang);
        tacoGroup[selectedTaco].userData.ang = 0;
      }
      tacoGroup[selectedTaco].add(mesh);
      changeColor = false;
      selectedTaco = 0; //the taco is no longer selected
    }
  }
}

function direcionaTaco() {
  if (selectedTaco != 0) {  //can only change angle if a taco has been selected
    if(right) {
      if( tacoGroup[selectedTaco].userData.ang <= Math.PI/3)  //angle limit
      {
        if(selectedTaco == 1 || selectedTaco == 2 ) {
           pivotGroup[selectedTaco].rotateY(Math.PI/1000);   //rotates taco pivot
           pivotBall[selectedBall].rotation.y += Math.PI/1000;  //rotates ball pivot
        }
        if(selectedTaco == 3) {
          pivotGroup[selectedTaco].rotateY(Math.PI/1000);
          pivotBall[selectedBall].rotation.y -= Math.PI/1000;
        }
        if(selectedTaco == 4 || selectedTaco == 5) {
          pivotGroup[selectedTaco].rotateY(Math.PI/1000);
          pivotBall[selectedBall].rotation.y -= Math.PI/1000;
        }
        if(selectedTaco == 6) {
           pivotGroup[selectedTaco].rotateY(Math.PI/1000);
           pivotBall[selectedBall].rotation.y += Math.PI/1000;
        }
        //updates angle
        tacoGroup[selectedTaco].userData.ang += Math.PI/1000;
        ballGroup[selectedBall].userData.ang += Math.PI/1000;
      }
   }

   if(left) {
     if( tacoGroup[selectedTaco].userData.ang >= -Math.PI/3)  //angle limit
     {
       if( selectedTaco == 1 || selectedTaco == 2){
          pivotGroup[selectedTaco].rotateY(-Math.PI/1000);    //rotates taco pivot
          pivotBall[selectedBall].rotation.y -= Math.PI/1000;  //rotates ball pivot
       }
       if(selectedTaco == 3) {
          pivotGroup[selectedTaco].rotateY(-Math.PI/1000);
          pivotBall[selectedBall].rotation.y += Math.PI/1000;
        }
       if(selectedTaco == 4 || selectedTaco == 5) {
          pivotGroup[selectedTaco].rotateY(-Math.PI/1000);
          pivotBall[selectedBall].rotation.y += Math.PI/1000;
       }
       if(selectedTaco == 6) {
          pivotGroup[selectedTaco].rotateY(-Math.PI/1000);
          pivotBall[selectedBall].rotation.y -= Math.PI/1000;
      }
        //updates angle
       tacoGroup[selectedTaco].userData.ang -= Math.PI/1000;
       ballGroup[selectedBall].userData.ang -= Math.PI/1000;
     }
   }
 }
}



function moveBall(number) {
  var vX, vZ;
  var  friction = 0.99;
  var ref = new THREE.Vector3();

  if(ballGroup[number].userData.moving == true) {
    vX = ballGroup[number].userData.speedinit;
    vZ = ballGroup[number].userData.speedinit;

    ref.set( vX, 0, vZ ).normalize();
    //v = v0 + a*t
    vX += 0.005*delta;
    vZ += 0.005*delta;

    //d = d0 + v
    pivotBall[number].translateX(vX);
    pivotBall[number].translateZ(vZ);

    ballGroup[number].userData.speedinit *=  friction;
    ballGroup[number].userData.speedinit *=  friction;
    var totalVelocity = Math.sqrt( vX * vX + vZ * vZ );

    // radius of ball = 1.5
    ballGroup[number].userData.angOnAxis = totalVelocity / ( Math.PI * 1.5 ) * Math.PI;

    ballGroup[number].rotateOnAxis(ref, ballGroup[number].userData.angOnAxis);
    if(totalVelocity < 0.001 ) {
      ballGroup[number].userData.moving = false;
      if(number == selectedBall){
        tacada = false;
        if( cameraAtiva == cameraPerspetivaMovel) cameraAtiva = cameraPerspetiva;
    }
  }
}
}

function daTacada() {
    if(tacada) {
      ballGroup[selectedBall].userData.moving = true;
      ballGroup[selectedBall].userData.playable = true;
      tacada = false;
      changeColor = true;
      tacoGroup[selectedBall].userData.deuTacada = 1;
    }
}

function checkBallPlace(ball){
  //places ball in its original place
  infinite = false;
  scene.remove(pivotBall[ball]);
  scene.remove(ballGroup[ball]);
  if(ball == 1)
    createBall(-25, 6, -25, 1, 0);
  if(ball == 2)
    createBall(25, 6, -25, 2, 0);
  if(ball == 3)
    createBall(45, 6, 0, 3, -Math.PI/2);
  if(ball == 4)
    createBall(25, 6, 25, 4, -Math.PI);
  if(ball == 5)
    createBall(-25, 6, 25, 5, -Math.PI);
  if(ball == 6)
    createBall(-45, 6, 0, 6, Math.PI/2);
}

function doInfinite() {
  if(infinite) {    //flag that says the ball fell in a hole
    //moves ball downwards
      pivotBall[ballInfinite].position.y -= 1.5;
  }
}

//detecting collisions
//with holes
function checkCollisionHoles(i) {
  //fall every ball created
    //fall every hole
    for(h = 1; h < 7; h++) {
      if(calculateDistHoles(pivotBall[i], holeGroup[h]) == holeGroup[h]) {
        //makes ball n moke to infinite
        infinite= true;
        ballInfinite = i;
      }
    }
}

function calculateDistHoles(pivotBall, hole) {
  var dx, dz;
  var vectorB = pivotBall.position;
  var vectorH = hole.position;
  //hole dimensions: radius = 3 ,  hight = 1
  //so sqrt((1.5)*(1.5) + (3)*(3)) = 3.36
  //ball dimensions: radius = 1.5
  //so min dist without colision = 1+1.58 = 2.58

  dx = vectorB.x - vectorH.x;
  dz = vectorB.z - vectorH.z;
  //a little more than 2.58 so it is better represented
  if(Math.sqrt( dx * dx + dz * dz ) <= 3.36)
    return hole;
  return 0;
}

//with walls
function checkColisionTopWall(pivot, i) {
  var vector = pivot.position;

  if(vector.z < -28 + 1.5 || vector.z == -28 + 1.5){
    pivot.position.z = -28 + 1.5;
    pivot.rotateY( Math.PI - (2*ballGroup[i].userData.ang));

  }
}

function checkColisionDownWall(pivot, i) {
  var vector = pivot.position;

  if(vector.z > 28 - 1.5 || vector.z == 28 - 1.5){
    pivot.position.z = 28 - 1.5;
    pivot.rotateY(Math.PI - (2*ballGroup[i].userData.ang));
  }
}

function checkColisionLeftWall(pivot, i) {
  var vector = pivot.position;

  if(vector.x < -48 + 1.5 || vector.x == -48 + 1.5){
    pivot.position.x = -48 + 1.5;
    pivot.rotateY(Math.PI - (2*ballGroup[i].userData.ang));
  }
}

function checkColisionRightWall(pivot, i) {
  var vector = pivot.position;

if(vector.x > 48 - 1.5 || vector.x == 48 - 1.5){
    pivot.position.x = 48 - 1.5;
    pivot.rotateY(Math.PI - (2*ballGroup[i].userData.ang));
  }
}

function checkCollisionBall(ball, i) {

    for(d=1; d < ballGroup.length; d++) {
      if(i != d) {
        var ball1pos = pivotBall[d].position;
        var ball2pos = ball.position;
        var dist = ball1pos.distanceTo(ball2pos);       //.distanceTo is the same operation as the one in the calculateDistHoles() function
        if(dist <= 3) {
          if(ballGroup[i].userData.moving && ballGroup[d].userData.playable) {
            pivotBall[d].rotation = ball.rotation;
            ball.rotateY(-Math.PI);

            if(ballGroup[i].userData.speedinit < 0.1) {
              ballGroup[i].userData.speedinit += 0.1;
            }
            ballGroup[d].userData.moving = true;
            ballGroup[d].userData.speedinit = ballGroup[i].userData.speedinit;
            moveBall(i);
            moveBall(d);
          }
        }
      }
    }
}


function checkColision(i) {
  checkColisionTopWall(pivotBall[i],i);
  checkColisionDownWall(pivotBall[i],i);
  checkColisionRightWall(pivotBall[i],i);
  checkColisionLeftWall(pivotBall[i],i);
  checkCollisionBall(pivotBall[i],i);
  checkCollisionHoles(i);
}

function update() {
  for(i=1; i < ballGroup.length; i++) {
    moveBall(i);
    checkColision(i);
  }
}

function animate() {
    'use strict';

    delta = clock.getDelta();

    mudaCorTaco();
    direcionaTaco();
    doInfinite();
    daTacada();
    setTimeout(update, 1000);
    render();
    requestAnimationFrame(animate);
}
