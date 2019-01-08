package com.trajet;
import java.util.*;
import java.lang.*;
import java.io.*; 

//for each generation 
//keep the 2 best solutions, then add mutation/crossover

public class GA {

	private int generation = 1;//generation
	private int n_generation; //number of generation to compute
	private int popSize;//size of one population
	private double mut_rate; //mutation ratio
	//private double cross_rate; //crossover ratio
	private int maxMutationNum; //Max digit of mutation
	private ArrayList<Individu> population;
	private ArrayList<Individu> nextPopulation;
	private int nb_ville;
	private int nb_chemin = 1;
	private int villeDepart;
	private Individu best;
	private int matrix_distance[][];
	private double totalScore = 0.0;
	private HashMap popDistance = new HashMap<Integer[],Double>();
	
	//constructor
	public GA(int nb_ville, int villeDepart, int matrix_distance[][]){
		this.nb_ville = nb_ville;
		this.villeDepart = villeDepart;
		this.population = new ArrayList<Individu>();
		this.nextPopulation = new ArrayList<Individu>();
		this.matrix_distance = matrix_distance;
		
		if(this.nb_ville > 3){
			for(int i = 1; i<this.nb_ville -1 ;i++){
				this.nb_chemin = i * this.nb_chemin;
			}
			
		}else{
			this.nb_chemin = 1;
		}
	}
	
	//Function to set all parameters that we need to continue the genetic algorithm
	public void setInit(){
		
		Scanner sc = new Scanner(System.in);
		System.out.println("----------------------------------------");
		System.out.println("Nombre de chemins differents possibles: " + this.nb_chemin);
		System.out.println("----------------------------------------");
		
		//ask for the size of a generation
		do{
			System.out.println("Taille d'une generation?");
			this.popSize = sc.nextInt();
		}while(this.popSize<=0);
		
		//test if the size of a generation is reasonable
		if(this.nb_chemin < this.popSize){
			System.out.println("----------------------------------------");
			this.popSize = this.nb_chemin;
			System.out.println("Taille d'une generation que vous avez entree est inferieure a nombre de chemins possibles,");
			System.out.println("la taille est modifiee automatiquement egale au nombre de chemins possibles.");
			System.out.println("----------------------------------------");
		}
		
		//ask for the number of generation to be generated
		do{
			System.out.println("Nombre de generations a generer?");
			this.n_generation = sc.nextInt();
		}while(this.n_generation<=0);
		
		//ask for the percentage of mutation
		do{
			System.out.println("Ratio de mutation?");
			this.mut_rate = sc.nextDouble();
		}while(this.mut_rate<=0);
		
		//ask for the max digits of mutation
		do{
			System.out.println("Nombre de chiffres maximum pour la mutation?");
			this.maxMutationNum = sc.nextInt();
		}while(this.maxMutationNum<=0);
		
	}
	
	//function to generate the first generation 
	public void initPopulation() {
		this.setInit();
		System.out.println("Generation initiale: ");
		int nb_pop=0;
		do{
			Individu route = new Individu(this.nb_ville, this.villeDepart,this.matrix_distance);
			route.setGene();
			
			//test if the route is already in the population
			if(!this.population.contains(route)){
				double distance = route.calculateDistance();
				this.totalScore += 1/distance;
				this.popDistance.put(route.setGene(),distance);
				this.population.add(route);
				nb_pop++;
			}
		}while(nb_pop < this.popSize);
		
		this.generation = 1;
		
		for(Individu ind : this.population){
			ind.printGene();
			System.out.println(". Sa distance est: "+ ind.getDistance());
		}
		
		this.best = this.getBestParent();
		System.out.println("Le meilleur chemin est: ");
		this.best.printGene();
		System.out.println(". Sa distance est: "+ this.best.getDistance());
		
		while(this.generation < this.n_generation){
			
			this.generateNewPop();
			this.best = this.getBest();
			System.out.println("Le meilleur chemin est: ");
			this.best.printGene();
			System.out.println(". Sa distance est: "+ this.best.getDistance());
		}
	}
	
	//function to generate one individu by mutation
	public Individu mutation(){
		Random random = new Random();
		
		//generate the number of digits need to be mutated
		int nb_digitMutation = random.nextInt(maxMutationNum)+1;
		int[] digitMutation = new int[nb_digitMutation];
		int nb_digit = 0;
		
		//generate the digits to be mutated
		while(nb_digit < nb_digitMutation){
			boolean digitExist = false;
			int digit;
			do{
				digitExist = false;
				digit = random.nextInt(this.nb_ville - 2) + 2;
				for(int i = 0; i<nb_digitMutation;i++){
					if(digitMutation[i] == digit){
						digitExist = true;
					}
				}
			}while(digitExist);
			
			if(!digitExist){
				digitMutation[nb_digit] = digit;
				nb_digit ++;
			}		
		}
		
		//choose a father individu
		Individu father = getIndividu();
		int[] gene = father.getGene();
		//int[] geneSon = new int[this.nb_ville+1];
		nb_digit = 0;
		
		//Mutation 
		while(nb_digit < nb_digitMutation){
			//choose a second digit to mutate
			for(int i = 0; i<nb_digitMutation;i++){
				int digit2;
				do{
					digit2 = random.nextInt(this.nb_ville - 2) + 2;
				}while(digit2 == digitMutation[i]);
				
				//mutation time!!
				if(digit2 != digitMutation[i]){
					int temp = gene[digit2];
					gene[digit2] = gene[digitMutation[i]];
					gene[digitMutation[i]] = temp;
					nb_digit ++;
				}
			}
		}
		
		//initialise the son individu
		Individu son = new Individu(this.nb_ville, this.villeDepart,this.matrix_distance);
		son.setGeneDirect(gene);
		
		return son;
	}
	
	//function to generate one individu by crossover
	public Individu crossover(Individu father,Individu mother,int geneStart,int geneEnd){
		int[] geneF = father.getGene();
		int[] geneM = mother.getGene();
		int[] gene = new int[geneM.length];
		
		for(int i = 0; i<this.nb_ville+1;i++){
			if(i > geneStart-1 && i < geneEnd-1) {
				gene[i] = geneM[i];
			}else{
				gene[i] = geneF[i];
			}
		}
		
		//handle conflicts if exist
		boolean conflict = false;
		do{
			System.out.println(conflict);
			int digit_1 = 0;
			int digit_2 = 0;
			for(int i = 0;i<geneM.length;i++){
				for(int j = 0;j<geneM.length;j++){
					if(i != j){
						if(gene[i] == gene[j]){
							conflict = true;
							digit_1 = i;
							digit_2 = j;
							break;	
						}
					}
				}
				if(conflict){
					break;
				}
			}
			
			//handle conflict !!
			if(conflict){
				//System.out.println(conflict);
				ArrayList<Integer> route = new ArrayList<Integer>();
				for (int i=0;i< this.nb_ville;i++)
				{
					route.add(i+1);
				}
				route.remove(this.villeDepart-1);
				
				ArrayList<Integer> geneList = new ArrayList<Integer>();
				for (int i=0;i< gene.length;i++)
				{
					geneList.add(gene[i]);
				}
				
				//find the disappeared cities
				for(int i = 0;i<route.size();i++){
					if(geneList.contains(route.get(i))){
						route.remove(i);
					}
				}
				if(route.size() > 0){
					System.out.println(gene[digit_1]);
					gene[digit_1] = route.get(0);
					System.out.println(gene[digit_1]);
					
					route.remove(0);
				}
				
				conflict = false;
			}
			//test again if conflict exists
			conflict = this.ifConflict(gene);
			
			//System.out.println(conflict);
		}while(conflict);
		
		Individu son = new Individu(this.nb_ville, this.villeDepart,this.matrix_distance);
		son.setGeneDirect(gene);
		return son;
	}
	
	public boolean ifConflict(int[] gene){
		boolean conflict = false;
		for(int i = 0;i<gene.length;i++){
			
			for(int j = 0;j<gene.length;j++){
				if(i != j){
					if(gene[i] == gene[j]){
						conflict = true;
						break;	
					}
				}
			}
			if(conflict){
				break;
			}
		}
		return conflict;
	}
	
	//function to get the BEST route of the former generation
	public Individu getBestParent(){
		double distance = 999999999;
		this.best = null;
		for(int i =0; i< this.population.size();i++){
			double dis = this.population.get(i).getDistance();
			if(distance > dis){
				distance = dis;
				this.best = this.population.get(i);
			}
		}
		return this.best;
	}
	
	public Individu getBest(){
		double distance = 999999999;
		this.best = null;
		for(int i =0; i< this.nextPopulation.size();i++){
			double dis = this.nextPopulation.get(i).getDistance();
			if(distance > dis){
				distance = dis;
				this.best = this.nextPopulation.get(i);
			}
		}
		return this.best;
	}
	
	//function to get randomly an individu
	public Individu getIndividu(){
		Random random = new Random();
		int index = random.nextInt(this.popSize);
		/*double selectPercent = random.nextDouble();
		double distributionPercent = 0.0;
		for(int i = 0; i< this.popSize; i++) {
			distributionPercent += this.population.get(i).getScore()/this.totalScore;
			if(distributionPercent > selectPercent) {
				return this.population.get(i);
			}
		}
		return null;*/
		return this.population.get(index);
	}
	
	//function to generate a new generation
	public void generateNewPop(){
		
		//verify that the NO.current generation is < than generation defined by user
		if(this.generation < this.n_generation){
			//switch the son generation to parent generation
			System.out.println("generation:"+this.generation);
			this.best = this.getBestParent();
			System.out.println("the best");
			this.best.printGene();
			if(this.generation > 1){
				this.population = null;
				this.population = new ArrayList<Individu>();
				System.out.println("here:"+this.generation);
				for(int i = 0; i < this.popSize; i++){
					//while(nextPopulation.size()>0){
					//System.out.println("before");
					//this.nextPopulation.get(0).printGene();
					this.population.add(this.nextPopulation.get(i));
					//this.nextPopulation.remove(0);
					//System.out.println("after");
					//this.nextPopulation.get(0).printGene();
					//}
				}
			}
			
			this.nextPopulation = null;
			this.nextPopulation = new ArrayList<Individu>();
			
			//add individus generate by mutation
			int mutation = (int)(this.mut_rate * this.popSize);
			this.nextPopulation.add(this.best);
			System.out.println("the best");
			this.best.printGene();
			//int counter = 0;
			do{
				Individu mut = mutation();
				if(!this.nextPopulation.contains(mut)){
					mut.calculateDistance();
					//System.out.println(this.nextPopulation.size());
					this.nextPopulation.add(mut);
					//mut.printGene();
					mutation--;
					//counter++;
				}
			//}while(counter < this.popSize-1);
			
			//add the best parent of the former generation
			
			
			}while(mutation>0);
			
			//add individus generate by crossover
			//number of individu that should be generated by crossover
			int crossover = this.popSize - 1 - mutation;
			
			do{
				//generate parents' individu
				Individu father = this.getIndividu();
				Individu mother = this.getIndividu();
				//has to be 2 different individus
				while(father == mother) {
					mother = this.getIndividu();
				}
				father.printGene();
				mother.printGene();
				//generate the start and end digits of the crossover
				Random random = new Random();
				int geneStart = random.nextInt(this.nb_ville - 2) + 2;
				int geneEnd = random.nextInt(this.nb_ville - 2) + 2;
				
				//make sure that 2 index are different
				while(geneStart == geneEnd) {
			
					geneEnd = random.nextInt(this.nb_ville - 2) + 2;
				}
				//make sure start is < than end
				if(geneStart > geneEnd) {
					
					int tmp = geneEnd;
					geneEnd = geneStart;
					geneStart = tmp;
				}
				
				//crossover time!!
				Individu cross = crossover(father,mother,geneStart,geneEnd);
			
				if(!this.nextPopulation.contains(cross)){
					this.nextPopulation.add(cross);
					crossover--;
				}
			}while(crossover>0);
		
			//print the new generation
			System.out.println("Generation " + (this.generation+1) +" is generated!");
			for(Individu ind : nextPopulation){
				ind.printGene();
				ind.calculateDistance();
				System.out.println(". Sa distance est: "+ ind.getDistance());
			}
			
			this.generation ++;
			
		}
	}
}
