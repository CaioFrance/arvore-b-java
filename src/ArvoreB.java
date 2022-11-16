import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class ArvoreB {

    private Node root;
    private int t; // grau minino

    private class Node {
        long[] chaves;
        int t; // grau minimo
        Node[] filhos;
        int totalChaves;
        boolean folha;

        Node(int t, boolean folha) {
            this.t = t;
            this.folha = folha;
            this.chaves = new long[2*t - 1]; // total de chaves
            this.filhos = new Node[2*t]; // total de filhos
            this.totalChaves = 0;
        }

        void insereNaoCheio(long valor) {
            // Inicializa o índice como índice do elemento mais à direita
            int index = totalChaves - 1;

            if (this.folha) {
                // Encontra a localização da nova chave a ser inserida
                // Move todas as chaves maiores para uma posição a frente
                while(index >= 0 && this.chaves[index] > valor) {
                    this.chaves[index+1] = chaves[index];
                    index--;
                }

                this.chaves[index+1] = valor;
                totalChaves++;
            } else {

                // Procurando o filho que vai ter a nova chave
                while(index >= 0 && chaves[index] > valor) {
                    index--;
                }

                // verifica se o filho está cheio
                if (this.filhos[index+1].totalChaves == 2*t - 1) {

                    // se estive cheio, faz a divisão
                    this.split(index+1, this.filhos[index+1]);

                    // a chave[index] sobe e é divida em dois. Verifica qual dos dois vai ter a nova chave
                    if (this.chaves[index+1] < valor) {
                        index++;
                    }
                }

                this.filhos[index+1].insereNaoCheio(valor);
            }
        }

        void split(int valor, Node y){
            // novo Nó que vai armazenar t-1 chaves
            Node aux = new Node(y.t, y.folha);
            aux.totalChaves = t - 1;

            int index;

            // Copia as ultimas chaves de y para aux
            for(index = 0; index < t - 1; index++) {
                aux.chaves[index] = y.chaves[index+t];
            }

            // Copia os ultimos T filhos de y para aux
            if (!y.folha) {
                for(index = 0; index < t; index++) {
                    aux.filhos[index] = y.filhos[index+t];
                }
            }

            // reduz o total de chaves no Nó passado por parâmetro
            y.totalChaves = t - 1;

            // Cria espaço do novo filho
            for(index = this.totalChaves; index >= valor+1; index--) {
                this.filhos[index+1] = this.filhos[index];
            }

            // Referencia o novo filho para o Nó aux
            this.filhos[valor+1] = aux;

            // Encontra a localização da nova chave e move todas as chaves maiores para um espaço a frente
            for(index = this.totalChaves - 1; index >= valor; index--) {
                this.chaves[index+1] = this.chaves[index];
            }

            // copia a chave do meio
            this.chaves[valor] = y.chaves[t-1];

            this.totalChaves++;
        }

        void correrNos() {
            // Possui N chaves e N+1 filhos, correrNos() passará pelas N chaves e os N+1 filhos
            int index;
            for(index = 0; index < this.totalChaves; index++) {
                if (!this.folha) {
                    this.filhos[index].correrNos();
                }
                System.out.println(this.chaves[index] + ": é folha ? " + this.folha);
            }

            if (!this.folha) {
                this.filhos[index].correrNos();
            }
        }

        Node busca(long valor) {
            int index = 0;
            while(index < this.totalChaves && valor > this.chaves[index]) {
                index++;
            }

            if (this.chaves[index] == valor) {
                return this;
            }

            if (folha) {
                return null;
            }

            return this.filhos[index].busca(valor);
        }
    }

    public ArvoreB(int t) {
        this.root = null;
        this.t = t;
    }

    void inserir(long valor) {
        // verifica se a arvore esta vazia
        if (root == null) {
            root = new Node(t, true);
            root.chaves[0] = valor;
            root.totalChaves = 1;
        } else {
            // verifica se a raiz está cheia
            if (root.totalChaves == 2*t - 1) {
                Node s = new Node(t, false); // aloca nova raiz

                s.filhos[0] = root; // raiz antiga vira filho da nova raiz

                s.split(0, root); // divide a raiz antiga e mova uma chave para a nova raiz

                int i = 0;
                if (s.chaves[0] < valor) {
                    i++;
                }
                s.filhos[i].insereNaoCheio(valor);

                root = s; // muda a raiz
            } else {
                // se não estiver cheio
                root.insereNaoCheio(valor);
            }
        }
    }

    void editar(long valorAtual, long novoValor) {
        Node n = root.busca(valorAtual);
        if (n != null) {
            int index;
            for(index = 0; index < n.chaves.length; index++) {
                if (n.chaves[index] == valorAtual) {
                    n.chaves[index] = novoValor;
                    break;
                }
            }

            Arrays.sort(n.chaves);
        }
    }

    void correrNos() {
        if (root != null) {
            root.correrNos();
        }
    }

    Node busca(long valor) {
        return root != null ? root.busca(valor) : null;
    }

    public static void main(String[] args) {
        // Grau mínimo 2
        ArvoreB b = new ArvoreB(2);
        String file = "files/indices.txt";
        try(Scanner scanner = new Scanner(new File(file))) {
            while(scanner.hasNext()) {
                String value = scanner.next().split(",")[0]; // corta a vírgula
                b.inserir(Long.parseLong(value)); // passa de valor texto para Long
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println("---------LISTA ATUAL----------");
        b.correrNos();

        System.out.println("\n------------ATUALIZOU-----------------\n");
        b.editar(1983035351L, 20);
        b.correrNos();

        System.out.println("\n-----------------BUSCA------------------\n");

        System.out.println("Existe o valor 20?");

        if (b.busca(20) != null){
            System.out.println("Existe");
        } else {
            System.out.println("Não existe");
        }

        System.out.println("---------------LISTA ATUALIZADA---------------------");
        b.correrNos();
    }

}
