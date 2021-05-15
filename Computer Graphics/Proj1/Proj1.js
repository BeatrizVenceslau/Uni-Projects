/*global THREE, requestAnimationFrame, console*/

var camera, cameraTopo, cameraLateral, cameraAtiva, cameraPerspetiva, scene, renderer;

var geometry, material, mesh,ang1, ang2, nSides, open, raio1, raio2, altura;

var ramo_1,ramo_2,ramo_3, axis;

var material_2, axes_ramo1, axes_ramo2, axes_ramo3;

var left, right, forward, backward;

var velocity = 0.005, mobileD = 0, clock;

function addCylinder(obj,x,y,z,nSides, open, raio1, raio2, ang1, ang2, altura){

  material_2 = new THREE.MeshBasicMaterial({ color: 0xffffff, transparent : false});
  geometry = new THREE.CylinderGeometry(raio1, raio2, altura, nSides, 1, open);
  mesh = new THREE.Mesh(geometry, material_2);
  mesh.position.set(x, y, z);
  mesh.rotateX(ang2);
  mesh.rotateZ(ang1);
  obj.add(mesh);
}

function addCube(obj,x,y,z, ang1, ang2, raio1, raio2){

  material_2 = new THREE.MeshBasicMaterial({ color: 0xff0000, transparent : false});
  geometry = new THREE.CubeGeometry(raio1, raio2, 0.5);
  mesh = new THREE.Mesh(geometry, material_2);
  mesh.position.set(x, y, z);
  mesh.rotateX(ang2);
  mesh.rotateZ(ang1);
  obj.add(mesh);

}

function addCone(obj,x,y,z, ang1, ang2){

  material_2 = new THREE.MeshBasicMaterial({ color: 0xff0000, transparent : false});
  geometry = new THREE.ConeGeometry( 5, 4, 6 );
  mesh = new THREE.Mesh(geometry, material_2);
  mesh.position.set(x, y, z);
  mesh.rotateX(ang2);
  mesh.rotateZ(ang1);
  obj.add(mesh);

}

function addHaste(obj,size,x,y,z,ang){

  geometry = new THREE.CylinderGeometry(0.5, 0.5, size, 20);
  mesh = new THREE.Mesh(geometry, material);
  mesh.position.set(x, y, z);
  mesh.rotateX(ang);
  obj.add(mesh);
}

function addFio(obj,size, x, y, z, nSizes){

  geometry = new THREE.CylinderGeometry(nSizes, nSizes, size, 20);
  mesh = new THREE.Mesh(geometry, material);
  mesh.position.set(x, y, z);
  obj.add(mesh);
}

function addHastePrincipal(obj,size,x,y,z,ang){

  geometry = new THREE.CylinderGeometry(0.5, 0.5, size, 20);
  mesh = new THREE.Mesh(geometry, material);
  mesh.position.set(x, y, z);
  mesh.rotateZ(ang);
  obj.add(mesh);


}


function createRamo1(x,y,z){

  ramo_1 = new THREE.Object3D();

  material = new THREE.MeshBasicMaterial({ color : 0x808080, transparent : false, wireframe : true});

  ramo_1.userData = { rotating: false};

  addFio(ramo_1, 80, 0, -10, 0, 0.2);
  addHastePrincipal(ramo_1, 70, 0, 15, 0, 1*Math.PI/2);
  addHaste(ramo_1, 40, -20, 15, 0, 1 *Math.PI/2);
  addHaste(ramo_1, 40, 20, 15, 0, 1 *Math.PI/2);
  addCylinder(ramo_1,-35.5, 12, 0, 5, false, 2, 5, Math.PI/2, Math.PI/2, 0.5);
  addCube(ramo_1,-20, 15, -20, 0, 0, 8, 8);
  addCylinder(ramo_1,-20, 15, 20, 3, false, 5, 5, 0, -Math.PI/2, 0.5);
  addCube(ramo_1,20, 15, 20, 0, 0, 9, 4);
  addCylinder(ramo_1, 35, 12, 0, 8, false, 7, 7, Math.PI/2, Math.PI/2, 0.5);
  addCone(ramo_1, 20, 15, -20, 0, -Math.PI/2);

  ramo_1.add(axes_ramo1);
  scene.add(ramo_1);

  ramo_1.position.set(x, y, z);
}

function createRamo2(x,y,z){

  ramo_2 = new THREE.Object3D();

  ramo_2.userData = { rotating: false};

  addHastePrincipal(ramo_2,100, 0, 5, 0, Math.PI/2);
  addHaste(ramo_2, 25, -35, 0, 0, 0);
  addHaste(ramo_2, 25, 35, 0, 0, 0);
  addHaste(ramo_2,25, -35, -5, 0, 1*Math.PI/2);
  addHaste(ramo_2,25, 35, -5, 0, 1*Math.PI/2);
  addCylinder(ramo_2,-35.5, -8, 13, 8, false, 2, 4, 0, Math.PI/2, 0.5);
  addCylinder(ramo_2,-35.5, -8, -13, 6, false, 4, 4, 0, Math.PI/2, 0.5);
  addCube(ramo_2, 35, -15, 0, 0, 0, 6, 6);
  addCube(ramo_2, -35, -14, 0, 0, 0, 6, 3);
  addCylinder(ramo_2,35.5, -8, 13, 7, false, 4, 4, 0, Math.PI/2, 0.5);
  addCylinder(ramo_2,35.5, -8, -13, 30, false, 4, 4, 0, Math.PI/2, 0.5);
  addCone(ramo_2, -50, 5, 0, Math.PI/2, Math.PI/2);
  addCone(ramo_2, 50, 5, 0, -Math.PI/2, Math.PI/2);

  ramo_2.add(axes_ramo2);
  scene.add(ramo_2);
  ramo_2.position.set(x, y, z);
}


function createRamo3(x,y,z){

  ramo_3 = new THREE.Object3D();

  ramo_3.userData = { rotating: false};

  addHaste(ramo_3,50, 0, -20, 0, 1*Math.PI/2);
  addHastePrincipal(ramo_3,50, 0, -20, 0, Math.PI/2);
  addCube(ramo_3, 28, -20, 0, 0,Math.PI/2, 6, 6);
  addCube(ramo_3, -28, -20, 0, 0, 0, 6, 6);
  addCylinder(ramo_3, 0, -20, 25, 7, false, 4, 4, 0, Math.PI/2, 0.5);
  addCylinder(ramo_3, 0, -20, -25, 9, false, 4, 4, 0, Math.PI/2, 0.5);

  ramo_3.add(axes_ramo3);
  scene.add(ramo_3);
  ramo_3.position.set(x, y, z);
}


function createScene() {
    'use strict';

    scene = new THREE.Scene();


    axes_ramo1 = new THREE.AxisHelper(10);
    axes_ramo2 = new THREE.AxisHelper(10);
    axes_ramo3 = new THREE.AxisHelper(10);
    createRamo1(0, 20, 0);
    createRamo2(0, -15, 0);
    createRamo3(0, -15, 0);
    ramo_1.add(ramo_2);
    ramo_2.add(ramo_3);

}

function createCamera() {
    'use strict';
    cameraPerspetiva =new THREE.PerspectiveCamera(70,
                                         window.innerWidth / window.innerHeight,
                                         1,
                                         1000);

    cameraTopo = new THREE.OrthographicCamera (200 / - 2, 200 / 2, 200 / 2, 200 / - 2, 1, 1000 );
    cameraTopo.position.x = 0;
    cameraTopo.position.y = 200;
    cameraTopo.position.z = 0;
    cameraTopo.lookAt (new THREE.Vector3(0, 1, 0));

    cameraLateral = new THREE.OrthographicCamera (200 / - 2, 200 / 2, 200 / 2, 200 / - 2, 1, 1000 );
    cameraLateral.position.x = 250;
    cameraLateral.position.y = 0;
    cameraLateral.position.z = 0;
    cameraLateral.lookAt (new THREE.Vector3(1, 0, 0));

    camera = new THREE.OrthographicCamera (200 / - 2, 200 / 2, 200 / 2, 200 / - 2, 1, 1000 );
    camera.position.x = 0;
    camera.position.y = 0;
    camera.position.z = 100;
    camera.lookAt(new THREE.Vector3(0, 0, 1));

    cameraPerspetiva.position.x = 0;
    cameraPerspetiva.position.y = 0;
    cameraPerspetiva.position.z = 100;
    cameraPerspetiva.lookAt(new THREE.Vector3(0, 0, 1));
    cameraAtiva = camera;                   //variavel que indica qual a camera ativa no momento
                                            // inicia com visao frontal
}

function onResize() {
    'use strict';

    renderer.setSize(window.innerWidth, window.innerHeight);

    if (window.innerHeight > 0 && window.innerWidth > 0) {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
    }
}

function onKeyDown(e) {
    'use strict';

    switch (e.keyCode) {
      case 37: //arrow left
        left = true;
        break;
      case 38: //arrow up
        forward = true;
        break;
      case 39: //arrow right
        right = true;
        break;
      case 40: //arrow down
        backward = true;
        break;
      case 81: //Q
      case 113: //q
        /*controlar o angulo do ramo1*/
        ramo_1.userData.rotating = !ramo_1.userData.rotating;
        if(velocity > 0 ) velocity = velocity*(-1);
        if(ramo_2.userData.rotating == true) ramo_2.userData.rotating = false;
        if(ramo_3.userData.rotating == true) ramo_3.userData.rotating = false;
        break;
      case 69:  //E
      case 101: //e
        /*controlar o angulo do ramo1*/
        ramo_1.userData.rotating = !ramo_1.userData.rotating;
        if(velocity < 0 ) velocity = velocity*(-1);
        if(ramo_2.userData.rotating == true) ramo_2.userData.rotating = false;
        if(ramo_3.userData.rotating == true) ramo_3.userData.rotating = false;
        break;
      case 65: //A
      case 97: //a
        /*controlar o angulo do ramo2*/
        if( ramo_1.userData.rotating == false){
          ramo_2.userData.rotating = !ramo_2.userData.rotating;
          if(velocity > 0 ) velocity = velocity*(-1);
          if(ramo_3.userData.rotating == true) ramo_3.userData.rotating = false;
        }
        break;
      case 68:  //D
      case 100: //d
        /*controlar o angulo do ramo2*/
        if( ramo_1.userData.rotating == false){
          ramo_2.userData.rotating = !ramo_2.userData.rotating;
          if(velocity < 0 ) velocity = velocity*(-1);
          if(ramo_3.userData.rotating == true) ramo_3.userData.rotating = false;
        }
        break;
      case 90: //Z
      case 122: //z
        /*controlar o angulo do ramo3*/
          if( ramo_1.userData.rotating == false && ramo_2.userData.rotating == false ){
            ramo_3.userData.rotating = !ramo_3.userData.rotating;
            if(velocity > 0 ) velocity = velocity*(-1);
          }
        break;
      case 67:  //C
      case 99: //c
        /*controlar o angulo do ramo3*/
        if( ramo_1.userData.rotating == false && ramo_2.userData.rotating == false ){
          ramo_3.userData.rotating = !ramo_3.userData.rotating;
          if(velocity < 0 ) velocity = velocity*(-1);
        }
        break;
    case 52: //4
        // ativa wireframe
        scene.traverse(function (node) {
            if (node instanceof THREE.Mesh) {
                node.material.wireframe = !node.material.wireframe;
            }
        });
        break;
    case 51: //3
        // muda camera selecionada para visao topo
        cameraAtiva = cameraLateral;
        break;
    case 50: //2
        // muda camera selecionada para visao lateral
        cameraAtiva = cameraTopo;
        break;
    case 49: //1
        // muda camera selecionada para visao frontal
        cameraAtiva = camera;
        break;
    case 54: //6
        //camera extra -> muda para perspetiva frontal
        cameraAtiva = cameraPerspetiva;
    }
}

function onKeyUp(e) {
    'use strict';

    switch (e.keyCode) {
      case 37: // arrow left
        left = false;
        break;
      case 38: // arrow up
        forward = false;
        break;
      case 39: // arrow right
        right = false;
        break;
      case 40: // arrow down
        backward = false;
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
    render();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
}

/*funcoes de movimento*/
function moveRight(aceleration, delta){
		if(mobileD < aceleration){
			mobileD += delta + 30*velocity;
		}
		else{
			mobileD = aceleration;
		}
}


function moveLeft(aceleration, delta){
		if(mobileD > -aceleration){
	 		mobileD -= delta + 30*velocity;
	 	}
	 	else {
	 		mobileD = -aceleration;
	 	}
}

function mobileMoveX(delta){

	if(left){
		moveLeft(1, delta);
	}
	else if(right){
		moveRight(1, delta);
	}
	else if(!left){
		moveRight(0, delta);
	}
	else if(!right){
		moveLeft(0, delta);
	}

	ramo_1.translateX(mobileD);
}

function mobileMoveZ(delta){

	if(forward){
		moveLeft(1, delta);
	}
	else if(backward){
		moveRight(1, delta);
	}
	else if(!forward){
		moveLeft(0, delta);
	}
	else if(!backward){
		moveRight(0, delta);
	}

	ramo_1.translateZ(mobileD);
}



function animate() {
    'use strict';

    var delta = clock.getDelta();

    mobileMoveX(delta);
    mobileMoveZ(delta);

    if (ramo_1.userData.rotating) {
      axis = new THREE.Vector3(0, 1, 0).normalize();
      ramo_1.rotateOnAxis(axis, velocity);
    }

    if (ramo_2.userData.rotating) {
      axis = new THREE.Vector3(0, 1, 0).normalize();
      ramo_2.rotateOnAxis(axis, velocity);
    }

    if (ramo_3.userData.rotating) {
      axis = new THREE.Vector3(0, 1, 0).normalize();
      ramo_3.rotateOnAxis(axis, velocity);
    }

    render();
    requestAnimationFrame(animate);
}
