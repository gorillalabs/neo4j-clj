{
  description = "A Nix-flake-based Node.js development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      pkgs = import nixpkgs { inherit system; };

      
    in {
      devShell = with pkgs;
        mkShell {
          packages = [
            jdk23
            clojure
            leiningen
            neo4j
            git
            zsh
          ];

          shellHook = ''
            echo "Clojure dev environment activated!"
            echo "Java version: $(java --version)"
            echo "Clojure CLI version: $(clj --version)"
            export NEO4J_HOME="./neo4j/"
            exec zsh
          '';
        };

    });
}
