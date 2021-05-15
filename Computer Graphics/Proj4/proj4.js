/*global THREE, requestAnimationFrame, console*/

/*
  Realizado por:
  Beatriz Venceslau ist193734
  Carolina Ramos    ist193694
  Diogo Cabral      ist193704
*/

//global variables
var scene, scene_2, renderer;
var reset = false;
var pause_screen = false;

//cameras
var activeCamera, cameraPersp, cameraPausem;
var controls;

//resize
var larg, comp, wind = 200;

//lights
var dirLight, pointLight;

//materials
var material, geometry, mesh, floor_material;
var texture, bumpMap, floor_texture, ball_texture;
var floor_mesh, flag_mesh, triangle_mesh, ball_mesh;
var floor_material, haste_material, triangle_material, ball_material;

//objects
var floor, flag, ball, flag_pivot;

//movement
var movingFlag = true;


//funtions
function createMaterials() {

  floor_texture = new THREE.TextureLoader().load('textures/relvado1.jpg');
  bumpMap = new THREE.TextureLoader().load('textures/relva.png');
  ball_texture = new THREE.TextureLoader().load('textures/golfBall.jpg');


  floor_material = new Array(3);
  floor_material[0] = new THREE.MeshBasicMaterial({ map: floor_texture, bumpMap: bumpMap, side: THREE.DoubleSide, wireframe:false });
  floor_material[1] = new THREE.MeshPhongMaterial ( { map: floor_texture, bumpMap: bumpMap, side: THREE.DoubleSide, wireframe:false } );

  haste_material = new Array(2);
  haste_material[0] = new THREE.MeshBasicMaterial( {color: 0xD3D3D3, wireframe:false} );
  haste_material[1] = new THREE.MeshPhongMaterial( {color: 0xD3D3D3, wireframe:false} );

  triangle_material = new Array(2);
  triangle_material[0] = new THREE.MeshBasicMaterial( {color: 0xFF0000, wireframe:false} );
  triangle_material[1] = new THREE.MeshPhongMaterial( {color: 0xFF0000, wireframe:false} );


  ball_material = new Array(2);
  ball_material[0] = new THREE.MeshBasicMaterial( {color: 0xFFFFFF,  map: ball_texture, wireframe:false} );
  ball_material[1] = new THREE.MeshPhongMaterial( {color: 0xFFFFFF,  map: ball_texture, wireframe:false} );


}

function createFloor(x, y, z) {
  'use strict';

  floor_texture.wrapS = THREE.RepeatWrapping;
  floor_texture.wrapT = THREE.RepeatWrapping;
  floor_texture.repeat.set(10, 10);
  bumpMap.wrapS = THREE.RepeatWrapping;
  bumpMap.wrapT = THREE.RepeatWrapping;
  bumpMap.repeat.set(10, 10);
  geometry = new THREE.CubeGeometry(400, 1, 400, 10, 10, 10);
  //material = new THREE. ( { map:floor_texture, bumpMap: bumpMap, side: THREE.DoubleSide, wireframe:false } );
  floor_mesh = new THREE.Mesh(geometry, floor_material[1]);
  floor_mesh.position.set(x, y, z);
  floor_mesh.receiveShadow = true;
  scene.add(floor_mesh);
}

function createFlag(x, y, z) {
  flag = new THREE.Object3D();
  flag.position.set(x, y, z);

  //haste
  geometry = new THREE.CylinderGeometry( 0.5, 0.5, 40, 10 );
  flag_mesh = new THREE.Mesh( geometry, haste_material[1] );
  flag_mesh.position.set(x, y, z);
  flag.add(flag_mesh);
  scene.add(flag);

  //triangle
  geometry = new THREE.CylinderGeometry( 6, 6, 1, 3 );
  triangle_mesh = new THREE.Mesh( geometry, triangle_material[1] );
  triangle_mesh.position.set(0,14,3);
  triangle_mesh.rotateZ(Math.PI/2);
  scene.add(triangle_mesh);

  flag_pivot = new THREE.Object3D();
  flag_pivot.position.set(x+50,y+10,z+30);
  flag_pivot.add(triangle_mesh);

  scene.add(flag_pivot);
}

function createBall(x, y, z) {
    ball = new THREE.Object3D();
    ball.userData = {movingBall: false, velocity:0};
    geometry = new THREE.SphereGeometry( 2, 32, 32 );
    ball_mesh = new THREE.Mesh( geometry, ball_material[1] );
    ball_mesh.position.set(x, y+2.5, z);
    ball.add(ball_mesh);
    scene.add( ball );
}

function createSkyBox() {
  var loader = new THREE.CubeTextureLoader();
  var texture = loader.load(['textures/nx.png', 'textures/px.png', 'textures/ny.png', 'textures/nz.png','textures/py.png', 'textures/pz.png']);
  scene.background = texture;
}

function createPauseScreen() {
  const pauseTexture = new THREE.TextureLoader().load( 'textures/pauseScreen.jpg' );
  geometry = new THREE.CubeGeometry(75, 10, 75, 10, 10, 10);
  material = new THREE.MeshBasicMaterial({ color: 0x696969, wireframe: false, map: pauseTexture });
  mesh = new THREE.Mesh(geometry, material);
  mesh.position.set(0,0,50);
  mesh.rotateX(Math.PI/2);
  scene_2.add(mesh);
}

function createScene() {
    'use strict';

    scene = new THREE.Scene();
    createMaterials();
    createFloor(0, 0, 0);
    createFlag(50, 10, 30);
    createBall(0, 0, 0);
    createSkyBox();

    scene_2 = new THREE.Scene();
    createPauseScreen();
}

//create cameras
function createCamera() {
  'use strict';

  createCamPresp();
  createCamPauseScreen();
  activeCamera = cameraPersp;
}

function createCamPresp() {
  var aspect = window.innerWidth / window.innerHeight;
  cameraPersp =new THREE.PerspectiveCamera(70, aspect, 1, 1000);
  cameraPersp.position.x = 120;
  cameraPersp.position.y = 30;
  cameraPersp.position.z = 120;

  controls = new THREE.OrbitControls(cameraPersp, renderer.domElement);
  controls.target.set(0,0, 0);
  controls.update();
}

function createCamPauseScreen() {
  var aspect = window.innerWidth / window.innerHeight;
  cameraPause = new THREE.OrthographicCamera(-wind * aspect/2, wind *aspect /2, wind / 2, -wind / 2, 1, 1000);
  cameraPause.position.x = 0;
  cameraPause.position.y = 0;
  cameraPause.position.z = 100;
  cameraPause.lookAt (new THREE.Vector3(0, 1, 0));
}

//create lights
function createLighting(){
  createDirectionalLight(0xFFFFFF, 0.5, -20, 110, 70);
  createPointLight(0xFFFF00,2,100,0,25,0);
}

function createDirectionalLight(color,itensity,x,y,z) {
  dirLight = new THREE.DirectionalLight(color, itensity);
  dirLight.position.set(x,y,z);
  dirLight.target.position.set( 0, 0, 0 );
  dirLight.target.updateMatrixWorld();
  scene.add(dirLight);
}


function createPointLight(color,itensity,distance,x,y,z) {
  pointLight = new THREE.PointLight(color, itensity, distance);
  pointLight.position.set(x,y,z);
  scene.add(pointLight);
}


//onResize
function onResize() {
    renderer.setSize(window.innerWidth, window.innerHeight);
    if (window.innerHeight > 0 && window.innerWidth > 0) {
      var aspect = window.innerWidth / window.innerHeight;
      var aspect1 = window.innerHeight / window.innerWidth;

      if(activeCamera == cameraPersp) { //prespCamera
        activeCamera.aspect = aspect;
      }
      else{ //sideCamera (Ortho)
        //decreased in length
        if(window.innerHeight > larg || window.innerHeight < larg) {
          cameraPause.right  = wind * aspect / 2;
          cameraPause.left   = -wind * aspect / 2;
          cameraPause.top    = wind / 2;
          cameraPause.bottom = -wind / 2;
          larg = window.innerHeight;
          comp = window.innerHeight;
        }
        //decreased in width
        else if(window.innerWidth > comp || window.innerWidth < comp) {
          cameraPause.right  = wind / 2;
          cameraPause.left   = -wind / 2;
          cameraPause.top    = wind * aspect1 / 2;
          cameraPause.bottom = -wind * aspect1 / 2;
          comp = window.innerWidth;
          larg = window.innerHeight;
        }
        activeCamera = cameraPause;
      }
      activeCamera.updateProjectionMatrix();
    }
}

function moveBall() {
  if (ball.userData.movingBall) {
      ball.userData.velocity += 0.06;
      ball.position.y = 20 * Math.abs((Math.sin(ball.userData.velocity)));
      ball.position.z += 5 * 0.1;
      ball.position.x += 5 * 0.1;
    }
}

function moveFlag() {
  if(!movingFlag);
  else flag_pivot.rotateY(0.15);
}

function changeWireframe() {
  if(floor_mesh.material.wireframe) {
    floor_mesh.material.wireframe = false;
    flag_mesh.material.wireframe = false;
    triangle_mesh.material.wireframe = false;
    ball_mesh.material.wireframe = false;
  }
  else {
    floor_mesh.material.wireframe = true;
    flag_mesh.material.wireframe = true;
    triangle_mesh.material.wireframe = true;
    ball_mesh.material.wireframe = true;
  }
}


function changeCalcusMaterial() {
  var wireframe1 = floor_mesh.material.wireframe;

  if(floor_mesh.material == floor_material[1]) {
    floor_mesh.material = floor_material[0];
    flag_mesh.material  = haste_material[0];
    triangle_mesh.material = triangle_material[0];
    ball_mesh.material = ball_material[0];
  }
  else {
    floor_mesh.material = floor_material[1];
    flag_mesh.material  = haste_material[1];
    triangle_mesh.material = triangle_material[1];
    ball_mesh.material = ball_material[1];
  }
  var wireframe2 = floor_mesh.material.wireframe;
  if(wireframe1 != wireframe2) changeWireframe();

}



function onKeyDown(e) {
    'use strict';

    switch (e.keyCode) {
        // ilumination
        case 68:  //D
        case 100: //d
            // turn directional light on and off
            if(!(pause_screen))dirLight.visible = !dirLight.visible;
            break;
        case 80:  //P
        case 112: //p
            // turn pontual light on and off
            if(!(pause_screen))pointLight.visible = !pointLight.visible;
            break;
        case 73:  //I
        case 105: //i
            // alternate between materials
            if(!(pause_screen))changeCalcusMaterial();
            break;

        //Wireframe
        case 87:  //W
        case 119: //w
            if(!(pause_screen))changeWireframe();
            break;

        //Ball movement
        case 66: //B
        case 98: //b
            if(!(pause_screen))ball.userData.movingBall = !ball.userData.movingBall;
            break;

        //Pause
        case 83:  //S
        case 115: //s
           pause_screen = !pause_screen;
           break;

        //Refresh
        case 82:  //R
        case 114: //r
            if(pause_screen) {
              reset = true;
            }
            break;
    }
}

function render() {
    'use strict';

      renderer.autoClear = false;
      renderer.clear();
      renderer.render(scene, cameraPersp);

      if (pause_screen) {
        renderer.clearDepth();
        renderer.render(scene_2, cameraPause);
      }
}


function init() {
    'use strict';
    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    createScene();
    createCamera();
    createLighting();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("resize", onResize);
}

function resetScene(){

    floor_mesh.material = floor_material[1];
    flag_mesh.material  = haste_material[1];
    triangle_mesh.material = triangle_material[1];
    ball_mesh.material = ball_material[1];

    floor_mesh.material.wireframe = false;
    flag_mesh.material.wireframe = false;
    triangle_mesh.material.wireframe = false;
    ball_mesh.material.wireframe = false;


    dirLight.visible = true;
    pointLight.visible = true;

    movingFlag = true;
    ball.position.set(0, 0, 0);
    ball.userData.movingBall = false;
    ball.userData.velocity = 0;

    cameraPersp.position.x = 120;
    cameraPersp.position.y = 30;
    cameraPersp.position.z = 120;
    controls.target.set(0,0, 0);
    controls.update();

    reset = false;
    pause_screen = false;
}

function animate() {
    'use strict';

    if(reset)resetScene();
    if(!(pause_screen))moveBall();
    if(!(pause_screen))moveFlag();
    render();
    requestAnimationFrame(animate);
}
