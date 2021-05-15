/*global THREE, requestAnimationFrame, console*/

/*
  Realizado por:
  Beatriz Venceslau ist193734
  Carolina Ramos    ist193694
  Diogo Cabral      ist193704
*/

//global variables
var scene, renderer;

//cameras
var activeCamera, cameraPersp, cameraSide;

//resize
var larg, comp, wind = 200;

//lights
var dirLight;
var spotLight = new Array(3);
var spotLightHelper = new Array(3);

//materials
var geometry, mesh;
var floor_material, palanque_material;
var chassi_material, carrocaria_material;
var glass_material, frontLight_material, backLight_material;
var holofoteLight_material, holofoteStruct_material;

//objects
var floor, base, car;
var holofote = new Array(3);

//mesh
var chassiTires = new Array(3);
var floor_mesh, base_mesh, chassi;
var carrocaria, glassFront, glassSide;

//movement
var left, right;


//funtions
function createMaterials() {
  floor_material = new Array(3);
  floor_material[0] = new THREE.MeshBasicMaterial({ color: 0x6B8E23, wireframe: false }); //flat shading
  floor_material[1] = new THREE.MeshLambertMaterial({ color: 0x6B8E23, wireframe: false }); //Gouraud
  floor_material[2] = new THREE.MeshPhongMaterial({ color: 0x6B8E23, wireframe: false, shininess: 100 }); //Phong

  palanque_material = new Array(3); //palanque is the same as base
  palanque_material[0] = new THREE.MeshBasicMaterial({ color: 0x8FBC8F, wireframe: false });
  palanque_material[1] = new THREE.MeshLambertMaterial({ color: 0x8FBC8F, wireframe: false });
  palanque_material[2] = new THREE.MeshPhongMaterial({ color: 0x8FBC8F, wireframe: false, shininess: 100 });

  chassi_material = new Array(3); //chassi and tires have the same material
  chassi_material[0] = new THREE.MeshBasicMaterial({ color: 0x696969, wireframe: false });
  chassi_material[1] = new THREE.MeshLambertMaterial({ color: 0x696969, wireframe: false });
  chassi_material[2] = new THREE.MeshPhongMaterial({ color: 0x696969, wireframe: false, shininess: 100 });

  carrocaria_material = new Array(3);
  carrocaria_material[0] = new THREE.MeshBasicMaterial({ color: 0x808080, wireframe: false });
  carrocaria_material[1] = new THREE.MeshLambertMaterial({ color: 0x808080, wireframe: false });
  carrocaria_material[2] = new THREE.MeshPhongMaterial({ color: 0x808080, wireframe: false, shininess: 100 });

  frontLight_material = new Array(3);
  frontLight_material[0] = new THREE.MeshBasicMaterial({ color: 0xF5F5DC, wireframe: false });
  frontLight_material[1] = new THREE.MeshLambertMaterial({ color: 0xF5F5DC, wireframe: false });
  frontLight_material[2] = new THREE.MeshPhongMaterial({ color: 0xF5F5DC, wireframe: false, shininess: 100 });

  backLight_material = new Array(3);
  backLight_material[0] = new THREE.MeshBasicMaterial({ color: 0xFF0000, wireframe: false });
  backLight_material[1] = new THREE.MeshLambertMaterial({ color: 0xFF0000, wireframe: false });
  backLight_material[2] = new THREE.MeshPhongMaterial({ color: 0xFF0000, wireframe: false, shininess: 100 });

  glass_material = new Array(3);
  glass_material[0] = new THREE.MeshBasicMaterial({ color: 0xF5F5F5, wireframe: false });
  glass_material[1] = new THREE.MeshLambertMaterial({ color: 0xF5F5F5, wireframe: false });
  glass_material[2] = new THREE.MeshPhongMaterial({ color: 0xF5F5F5, wireframe: false, shininess: 100 });

  holofoteStruct_material = new THREE.MeshBasicMaterial({ color: 0x808080, wireframe: false });
  holofoteLight_material = new THREE.MeshBasicMaterial({ color: 0xFFFF00, wireframe: false });
}

function createFloor(x, y, z) {
  'use strict';
  floor = new THREE.Object3D();
  geometry = new THREE.CubeGeometry(200, 1, 200, 10, 10, 10);
  floor_mesh = new THREE.Mesh(geometry, floor_material[0]);
  floor_mesh.position.set(x, y, z);
  floor.add(floor_mesh);
  scene.add(floor);
}

function createBase(x, y, z) {
  'use strict';
  base = new THREE.Object3D();
  geometry = new THREE.CylinderGeometry(70, 70, 10, 360);
  base_mesh = new THREE.Mesh(geometry, palanque_material[0]);
  base_mesh.position.set(x, y, z);
  base.add(base_mesh);
  base.add(car);
  scene.add(base);
}

function addChassi(obj, x, y, z) {
  geometry = new THREE.CubeGeometry(120, 5, 40);
  chassi = new THREE.Mesh(geometry, chassi_material[0]);
  chassi.position.set(x + 2, y, z);
  obj.add(chassi);
}

function addTires(obj, x, y, z, i) {
  geometry = new THREE.CylinderGeometry(10, 10, 6, 360);
  chassiTires[i] = new THREE.Mesh(geometry, chassi_material[0]);
  chassiTires[i].rotateX(Math.PI/2);
  chassiTires[i].position.set(x, y, z);
  obj.add(chassiTires[i]);
}

function addStructure(obj, x, y, z) {
  const geometry = new THREE.Geometry();

  //create vertices
  geometry.vertices.push(
    //right side
    new THREE.Vector3( 62,  -5, 20 ),        //0
    new THREE.Vector3( 62, 10, 20 ),         //1
    new THREE.Vector3(  15, 32, 18 ),        //2
    new THREE.Vector3( -60, -5, 20 ),        //3
    new THREE.Vector3( -62, 12, 20 ),        //4

    //left side
    new THREE.Vector3( 62,  -5, -20 ),       //5
    new THREE.Vector3( 62, 10, -20 ),        //6
    new THREE.Vector3(  15, 32, -18 ),       //7
    new THREE.Vector3( -60, -5, -20 ),       //8
    new THREE.Vector3( -62, 12, -20 ),       //9

    //front edge
    new THREE.Vector3( 70, 7, 15 ),          //10
    new THREE.Vector3( 70, 7, -15 ),         //11

    //right glass
    new THREE.Vector3( 50, 14, 19.8 ),       //12
    new THREE.Vector3( 15, 30, 18.2 ),       //13
    new THREE.Vector3( -16, 22, 18.4 ),      //14
    new THREE.Vector3( -15, 14, 19.8 ),      //15

    //left glass
    new THREE.Vector3( 50, 14, -19.8 ),      //16
    new THREE.Vector3( 15, 30, -18.2 ),      //17
    new THREE.Vector3( -16, 22, -18.4 ),     //18
    new THREE.Vector3( -15, 14, -19.8 ),     //19

    //front edge bottom
    new THREE.Vector3( 70, -5, 15 ),         //20
    new THREE.Vector3( 70, -5, -15 ),        //21

    //inclination of the side
    new THREE.Vector3( -16, 24, 18.1 ),      //22
    new THREE.Vector3( -16, 24, -18.1 ),     //23
  );

  //right side
  geometry.faces.push(new THREE.Face3( 0, 1, 4), new THREE.Face3( 4, 3, 0));
  geometry.faces.push(new THREE.Face3( 2, 12, 1), new THREE.Face3( 12, 2, 13),
                      new THREE.Face3( 14, 13, 2), new THREE.Face3( 22, 14, 2),
                      new THREE.Face3( 15, 22, 4), new THREE.Face3( 1, 15, 4),
                      new THREE.Face3( 12, 15, 1));
  //left side
  geometry.faces.push(new THREE.Face3( 9, 6, 5), new THREE.Face3( 5, 8, 9));
  geometry.faces.push(new THREE.Face3( 6, 16, 7), new THREE.Face3( 17, 7, 16),
                      new THREE.Face3( 7, 17, 18), new THREE.Face3( 7, 18, 23),
                      new THREE.Face3( 9, 23, 19), new THREE.Face3( 9, 19, 6),
                      new THREE.Face3( 6, 19, 16));
  //front edge
  geometry.faces.push(new THREE.Face3( 1, 10, 11), new THREE.Face3( 11, 6, 1));
  //back
  geometry.faces.push(new THREE.Face3( 9, 4, 2), new THREE.Face3( 9, 2, 7));
  geometry.faces.push(new THREE.Face3( 3, 4, 9), new THREE.Face3( 9, 8, 3));
  //front
  geometry.faces.push(new THREE.Face3( 1, 6, 7), new THREE.Face3( 1, 7, 2));
  geometry.faces.push(new THREE.Face3( 21, 11, 10), new THREE.Face3( 20, 21, 10));
  geometry.faces.push(new THREE.Face3( 1, 0, 10), new THREE.Face3( 0, 20, 10));
  geometry.faces.push(new THREE.Face3( 5, 6, 11), new THREE.Face3( 11, 21, 5));
  geometry.faces.push(new THREE.Face3( 21, 5, 0), new THREE.Face3( 0, 20, 21));

  geometry.computeFaceNormals();

  carrocaria = new THREE.Mesh(geometry, carrocaria_material[0]);
  carrocaria.position.set(x,y,z);
  obj.add(carrocaria);
}

function addSideGlasses(obj, x, y, z) {
  const geometry = new THREE.Geometry();

  //create vertices
  geometry.vertices.push(
    //right glass
    new THREE.Vector3( 50, 14, 19.8 ),       //0
    new THREE.Vector3( 15, 30, 18.2 ),       //1
    new THREE.Vector3( -16, 22, 18.4 ),      //2
    new THREE.Vector3( -15, 14, 19.8 ),      //3

    //left glass
    new THREE.Vector3( 50, 14, -19.8 ),      //4
    new THREE.Vector3( 15, 30, -18.2 ),      //5
    new THREE.Vector3( -16, 22, -18.4 ),     //6
    new THREE.Vector3( -15, 14, -19.8 ),     //7
  );

  geometry.faces.push(new THREE.Face3( 3, 0, 1), new THREE.Face3( 3, 1, 2));
  geometry.faces.push(new THREE.Face3( 5, 4, 7), new THREE.Face3( 6, 5, 7));
  geometry.computeFaceNormals();

  glassSide = new THREE.Mesh(geometry, glass_material[0]);
  glassSide.position.set(x,y,z);
  obj.add(glassSide);
}

function addFrontGlass(obj, x, y, z) {
  geometry = new THREE.CubeGeometry(34, 0.5, 30, 10, 10, 10);
  glassFront = new THREE.Mesh(geometry, glass_material[0]);
  //angle calculated from the inclination of vertices 1,2 and 6,7 from the car structure
  glassFront.rotateZ(-0.44);
  glassFront.position.set(x,y,z);
  obj.add(glassFront);
}

function addFrontLights(obj, x, y, z, rot) {
  geometry = new THREE.CubeGeometry(8, 1, 8, 10, 10, 10);
  mesh = new THREE.Mesh(geometry, frontLight_material[0]);
  mesh.rotateZ(rot);
  mesh.position.set(x,y,z);
  obj.add(mesh);
}

function addBackLight(obj, x, y, z) {
  geometry = new THREE.CubeGeometry(1, 2, 35, 10, 10, 10);
  mesh = new THREE.Mesh(geometry, backLight_material[0]);
  mesh.position.set(x,y,z);
  obj.add(mesh);
}

function createCar(x, y, z) {
  car = new THREE.Object3D();

  addChassi(car, x, y - 7.5, z);
  addStructure(car, x, y, z);
  addSideGlasses(car, x, y, z);
  addFrontGlass(car, x + 36, y + 22, z);
  addFrontLights(car, x + 70, y, z + 10, Math.PI/2);
  addFrontLights(car, x + 70, y, z - 10, Math.PI/2);
  addBackLight(car, x - 61.5, y + 10, z);

  addTires(car, x + 50, y - 10, z + 23, 0);
  addTires(car, x - 40, y - 10, z + 23, 1);
  addTires(car, x + 50, y - 10, z - 23, 2);
  addTires(car, x - 40, y - 10, z - 23, 3);
  scene.add(car);
}

function createScene() {
    'use strict';

    scene = new THREE.Scene();
    createMaterials();
    createFloor(0, 0, 0);
    createCar(0, 30, 0);
    createBase(0, 5, 0);
}

//create cameras
function createCamera() {
  'use strict';

  createCamSide();
  createCamPresp();

  //activeCamera - the camera that is being used at the moment
  //begins with perspective camera
  activeCamera = cameraPersp;
}

function createCamSide() {
  var aspect = window.innerWidth / window.innerHeight;
  cameraSide = new THREE.OrthographicCamera(-wind * aspect/2, wind *aspect /2, wind / 2, -wind / 2, 1, 1000);

  cameraSide.position.x = 0;
  cameraSide.position.y = 0;
  cameraSide.position.z = 100;
  cameraSide.lookAt (new THREE.Vector3(0, 1, 0));

  //keep track of the last window size
  larg = window.innerHeight;
  comp = window.innerWidth;
}

function createCamPresp() {
  var aspect = window.innerWidth / window.innerHeight;
  cameraPersp =new THREE.PerspectiveCamera(70, aspect, 1, 1000);
  cameraPersp.position.x = 125;
  cameraPersp.position.y = 125;
  cameraPersp.position.z = 125;

  cameraPersp.lookAt(new THREE.Vector3(0, 0, 0));
}

//create lights
function createLighting(){
  createDirectionalLight(0xFFFFFF, 0.8, -20, 110, 70);
  createSpotlight(0,0,95,0,0xFFFF00,0.5,0,Math.PI/4,0.2,10);
  createSpotlight(1,-100,80,0,0xFFFF00,0.5,0,Math.PI/6,0.2,10);
  createSpotlight(2,100,80,0,0xFFFF00,0.5,0,Math.PI/6,0.2,10);
}

function createDirectionalLight(color,itensity,x,y,z) {
  dirLight = new THREE.DirectionalLight(color, itensity);
  dirLight.position.set(x,y,z);
  dirLight.target.position.set( 0, 0, 0 );
	dirLight.target.updateMatrixWorld();
	scene.add(dirLight);
}

function createSpotlight(i,x,y,z,color,itensity,distance,angle,penumbra,decay){
  spotLight[i] = new THREE.SpotLight(color,itensity,distance,angle,penumbra,decay);
  spotLight[i].position.set(x,y,z);
  spotLight[i].target.position.set(0,0,0);

  spotLightHelper[i] = new THREE.SpotLightHelper(spotLight[i]);

  scene.add(spotLight[i]);
  scene.add(spotLightHelper[i]);
  createHolofote(i, x, y, z);
}

function createHolofote(i, x, y, z){
  holofote[i] = new THREE.Object3D();
  holofote[i].position.set(x,y,z);
  geometry = new THREE.ConeGeometry(2.5, 7, 64, 1, 0, 6.3);
  meshStruct = new THREE.Mesh(geometry, holofoteStruct_material);
  holofote[i].add(meshStruct);

  addHolofoteLight(i);

  if(i == 1){ //left holofote
    holofote[i].rotateY(-Math.PI/2);
    holofote[i].rotateX(Math.PI/3.5);
  }
  if(i == 2){ //right holofote
    holofote[i].rotateY(Math.PI/2);
    holofote[i].rotateX(Math.PI/3.5);
  }

  scene.add(holofote[i]);
}

function addHolofoteLight(i) {
  geometry = new THREE.SphereGeometry(3, 32, 32, 0, 6.3, 0, 1);
  meshLight = new THREE.Mesh(geometry, holofoteLight_material);

  meshLight.position.set(0, -1.8, 0);
  meshLight.rotateX(Math.PI);

  holofote[i].add(meshLight);
}

//ON/OFF button changes
function turnOnOffLight(i){
  if(i != -1 ) { //Turn ON/OFF SpotLight i
    spotLight[i].visible = !spotLight[i].visible;
    spotLightHelper[i].visible = !spotLightHelper[i].visible;
  }
  else {  //Turn ON/OFF Directional Light
    dirLight.visible = !dirLight.visible;
  }
}

function changeCalcusMaterial() {
  //the frontLights and backLights dont change
  //because they allow the observer to see the car even when no lights are on (at night)
  if(floor_mesh.material == floor_material[0]) {
    floor_mesh.material = floor_material[1];
    base_mesh.material  = palanque_material[1];
    chassi.material     = chassi_material[1];
    carrocaria.material = carrocaria_material[1];
    glassSide.material  = glass_material[1];
    glassFront.material = glass_material[1];
    for(i=0; i<4; i++) {
      chassiTires[i].material = chassi_material[1];
    }
  }
  else if(floor_mesh.material == floor_material[1] || floor_mesh.material == floor_material[2]) {
    floor_mesh.material = floor_material[0];
    base_mesh.material  = palanque_material[0];
    chassi.material     = chassi_material[0];
    carrocaria.material = carrocaria_material[0];
    glassSide.material  = glass_material[0];
    glassFront.material = glass_material[0];
    for(i=0; i<4; i++) {
      chassiTires[i].material = chassi_material[0];
    }
  }
}

function changeShading() {
  //the frontLights and backLights dont change
  //because they allow the observer to see the car even when no lights are on (at night)
  if(floor_mesh.material == floor_material[1]) {
    floor_mesh.material = floor_material[2];
    base_mesh.material  = palanque_material[2];
    chassi.material     = chassi_material[2];
    carrocaria.material = carrocaria_material[2];
    glassSide.material  = glass_material[2];
    glassFront.material = glass_material[2];
    for(i=0; i<4; i++) {
      chassiTires[i].material = chassi_material[2];
    }
  }
  else if(floor_mesh.material == floor_material[2]) {
    floor_mesh.material = floor_material[1];
    base_mesh.material  = palanque_material[1];
    chassi.material     = chassi_material[1];
    carrocaria.material = carrocaria_material[1];
    glassSide.material  = glass_material[1];
    glassFront.material = glass_material[1];
    for(i=0; i<4; i++) {
      chassiTires[i].material = chassi_material[1];
    }
  }
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
          cameraSide.right  = wind * aspect / 2;
          cameraSide.left   = -wind * aspect / 2;
          cameraSide.top    = wind / 2;
          cameraSide.bottom = -wind / 2;
          larg = window.innerHeight;
          comp = window.innerHeight;
        }
        //decreased in width
        else if(window.innerWidth > comp || window.innerWidth < comp) {
          cameraSide.right  = wind / 2;
          cameraSide.left   = -wind / 2;
          cameraSide.top    = wind * aspect1 / 2;
          cameraSide.bottom = -wind * aspect1 / 2;
          comp = window.innerWidth;
          larg = window.innerHeight;
        }
        activeCamera = cameraSide;
      }
      activeCamera.updateProjectionMatrix();
    }
}

function onKeyDown(e) {
    'use strict';

    switch (e.keyCode) {
        // rotation
        case 37: //arrow left
            left = true;
            break;
        case 39: //arrow right
            right = true;
            break;

        // ilumination
        case 81:  //Q
        case 113: //q
            // turn directional light on and off
            turnOnOffLight(-1);
            break;
        case 87:  //W
        case 119: //w
            // calculate ilumination or not
            changeCalcusMaterial();
            break;
        case 69:  //E
        case 101: //e
            // alternate between Gouraud and Phong
            changeShading();
            break;
        case 49: //1
            // turn on and off spotlight 0 (Top one)
            turnOnOffLight(0);
            break;
        case 50: //2
            // turn on and off spotlight 1 (Left one)
            turnOnOffLight(1);
            break;
        case 51: //3
            // turn on and off spotlight 2 (Right one)
            turnOnOffLight(2);
            break;

        // cameras
        case 52: //4
            // changes to perspective camera
            activeCamera = cameraPersp;
            break;
        case 53: //5
            // changes to lateral orthogonal camera
            activeCamera = cameraSide;
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
      renderer.render (scene,activeCamera);
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

    turnOnOffLight(0);
    turnOnOffLight(1);
    turnOnOffLight(2);

    animate();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
    window.addEventListener("resize", onResize);
}

//Movement
function rotateBase() {
  var axis, velocity = 0.015;

  if(right) {
    axis = new THREE.Vector3(0, 1, 0).normalize();
    base.rotateOnAxis(axis, velocity);
  }
  if(left) {
    axis = new THREE.Vector3(0, 1, 0).normalize();
    base.rotateOnAxis(axis, -velocity);
  }
}

function animate() {
    'use strict';

    rotateBase();
    render();
    requestAnimationFrame(animate);
}
