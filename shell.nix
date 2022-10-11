with import <nixpkgs> {};
mkShell {
  buildInputs = [
    openjdk11
  ];
  shellHook = ''
    export LD_LIBRARY_PATH=${pkgs.zlib}/lib:$LD_LIBRARY_PATH
'';
}