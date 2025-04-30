{
  description = "Java Spring Boot development environment";

  inputs = { nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable"; };

  outputs = { self, nixpkgs }:
    let
      # Helper function to create a devShell for a specific system
      mkDevShell = system:
        let
          pkgs = import nixpkgs {
            inherit system;
            config.allowUnfree = true;
          };
        in {
          default = pkgs.mkShell {
            buildInputs = with pkgs; [
              jdk23
              jdt-language-server
              maven
              spring-boot-cli
            ];
          };
        };
    in {
      devShells = {
        x86_64-linux = mkDevShell "x86_64-linux";
        aarch64-darwin = mkDevShell "aarch64-darwin";
      };
    };
}

