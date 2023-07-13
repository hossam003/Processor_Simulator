import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main {
	private static Object[] memory = new Object[2048]; 
	private static int[] gprs = new int[32];
	private static int pc = 0;
	
	private static int temp;
	private static int temp2;
	private static int temp3;
	private static int opcode;
	private static int r1;
	private static int r2;
	private static int r3;
	private static int shamt;
	private static int imm;
	private static int address;
	
	private static int pipeExecopcode;
	private static int pipeExectemp;
	private static int pipeExectemp2;
	private static int pipeExectemp3;
	private static int pipeExecr1;
	private static int pipeExecr2;
	private static int pipeExecr3;
	private static int pipeExecimm;
	private static int pipeExecaddress;
	private static int pipeExecshamt;
	
	private static int pipeMemopcode;
	private static int pipeMemtemp;
	private static int pipeMemtemp2;
	private static int pipeMemr1;
	private static int pipeMemr2;
	private static int pipeMemimm;

	private static int pipeWBopcode;
	private static int pipeWBtemp;
	private static int pipeWBr1;

	private static int pipetempc;
	private static int pipeJpc;
	
	private static int programsize;
	private static String fetched_ins;
	private static int loop = 0;

	private static boolean status = false;
	private static boolean flagc2 = false;
	private static boolean flagJBNE = false;
	private static int removeins1;
	private static int removeins2;
	private static int ri1ex;
	private static int ri1mem;
	private static int ri1wb;
	private static int ri2de;
	private static int ri2ex;
	private static int ri2mem;
	private static int ri2wb;

	
	private static boolean flagFD = false;
	private static boolean flagDE = false;
	private static boolean flagMW = false;
	private static boolean flagEM = false;
	
	private static int c1;
	private static int c2;
	

	
	
	public static String convert(String register) {
		
		switch(register) {
			case "R0": return "00000";
			case "R1": return "00001";
			case "R2": return "00010";
			case "R3": return "00011";
			case "R4": return "00100";
			case "R5": return "00101";
			case "R6": return "00110";
			case "R7": return "00111";
			case "R8": return "01000";
			case "R9": return "01001";
			case "R10": return "01010";
			case "R11": return "01011";
			case "R12": return "01100";
			case "R13": return "01101";
			case "R14": return "01110";
			case "R15": return "01111";
			case "R16": return "10000";
			case "R17": return "10001";
			case "R18": return "10010";
			case "R19": return "10011";
			case "R20": return "10100";
			case "R21": return "10101";
			case "R22": return "10110";
			case "R23": return "10111";
			case "R24": return "11000";
			case "R25": return "11001";
			case "R26": return "11010";
			case "R27": return "11011";
			case "R28": return "11100";
			case "R29": return "11101";
			case "R30": return "11110";
			case "R31": return "11111";
		}
		
		return null;
		
	}
	
	
	
	public static String convert(int decimal, int bits) {
        String binary = Integer.toBinaryString(decimal);
        while(binary.length() < bits) {
        	binary = "0" + binary;
        }
        return binary;
    }
	
	
	
	public static String convert2s(int decimal, int bits) {
		String binary = "";
		if(decimal < 0) {
			decimal = -decimal;
	        binary = Integer.toBinaryString(decimal);
	        while(binary.length() < bits) {
	        	binary = "0" + binary;
	        }
	        binary = flipBinaryString(binary);
	        binary = addBinaryString(binary);
		}
		else 
			binary = convert(decimal, bits);
		
		return binary;
	}
	
	
	
	public static String flipBinaryString(String binaryString) {
        String flippedBinaryString = "";
        for (char c : binaryString.toCharArray()) {
            if (c == '0') {
                flippedBinaryString += '1';
            } else {
                flippedBinaryString += '0';
            }
        }
        return flippedBinaryString;
    }
	
	
	
	public static String addBinaryString(String binaryString) {
        StringBuilder result = new StringBuilder();
        int carry = 1;
        for (int i = binaryString.length() - 1; i >= 0; i--) {
            int digit = binaryString.charAt(i) - '0' + carry;
            carry = digit / 2;
            result.append(digit % 2);
        }
        if (carry > 0) {
            result.append(carry);
        }
        return result.reverse().toString();
    }
	
	
	
	public static int convertback(String binary) {
		int decimal = 0;
		if(binary.charAt(0) == '1') {
			binary = flipBinaryString(binary);
			binary = addBinaryString(binary);
			decimal = Integer.parseInt(binary, 2);
			return -decimal;
			
		}
		else
			decimal = Integer.parseInt(binary, 2);
		return decimal;
		
	}
	
	
	
	public static void readProgram() {
		int i = 0;
		String filePath = "src/programs/program.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	String[] bin = line.split(" ");
            	switch(bin[0]) {
            		case "ADD": line = "0000" + convert(bin[1]) + convert(bin[2]) + convert(bin[3]) + "0000000000000"; break;
            		case "SUB": line = "0001" + convert(bin[1]) + convert(bin[2]) + convert(bin[3]) + "0000000000000"; break;
            		case "MULI": line = "0010" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            		case "ADDI": line = "0011" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            		case "BNE": line = "0100" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            		case "ANDI": line = "0101" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            		case "ORI": line = "0110" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            		case "J": line = "0111" + convert(Integer.parseInt(bin[1]), 28); break;
            		case "SLL": line = "1000" + convert(bin[1]) + convert(bin[2]) + "00000" + convert(Integer.parseInt(bin[3]), 13); break;
            		case "SRL": line = "1001" + convert(bin[1]) + convert(bin[2]) + "00000" + convert(Integer.parseInt(bin[3]), 13); break;
            		case "LW": line = "1010" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            		case "SW": line = "1011" + convert(bin[1]) + convert(bin[2]) + convert2s(Integer.parseInt(bin[3]), 18); break;
            	}
            	
            	memory[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        programsize = i;
	}
	
	
	
	public static void fetch() {
		fetched_ins = (String) memory[pc++];
	}
		
	public static void decode() {
		
		opcode = Integer.parseInt(fetched_ins.substring(0,4), 2);
		r1 = Integer.parseInt(fetched_ins.substring(4,9), 2);
		r2 = Integer.parseInt(fetched_ins.substring(9,14), 2);
		r3 = Integer.parseInt(fetched_ins.substring(14,19), 2);
		shamt = Integer.parseInt(fetched_ins.substring(19), 2);
		imm = convertback(fetched_ins.substring(14));
		address = Integer.parseInt(fetched_ins.substring(4),2);
		
//		if(opcode == 0 || opcode == 1 || opcode == 8 || opcode == 9)
//			memory[loop++] = "" + fetched_ins.substring(0,4) +" "+ fetched_ins.substring(4,9) +" "+ fetched_ins.substring(9,14) +" "+ fetched_ins.substring(14,19) +" "+ fetched_ins.substring(19);
//		else if(opcode == 7)
//			memory[loop++] = "" + fetched_ins.substring(0,4) +" "+ fetched_ins.substring(4);
//		else
//			memory[loop++] = "" + fetched_ins.substring(0,4) +" "+ fetched_ins.substring(4,9) +" "+ fetched_ins.substring(9,14) +" "+ fetched_ins.substring(14);
		
		temp = gprs[r1];
		temp2 = gprs[r2];
		if(opcode == 0 || opcode == 1)
			temp3 = gprs[r3];
		
	}
	
	public static void execute() {
		
		switch(pipeExecopcode) {
	/*ADD*/	case 0: pipeExectemp = pipeExectemp2 + pipeExectemp3; break;

	/*SUB*/	case 1: pipeExectemp = pipeExectemp2 - pipeExectemp3; break;

   /*MULI*/ case 2: pipeExectemp = pipeExectemp2 * pipeExecimm; break;

   /*ADDI*/ case 3: pipeExectemp = pipeExectemp2 + pipeExecimm; break;

	/*BNE*/	case 4: if(pipeExectemp != pipeExectemp2) {pc = pc - 2 + pipeExecimm; flagJBNE = true;} break;

   /*ANDI*/ case 5: pipeExectemp = pipeExectemp2 & pipeExecimm; break;

	/*ORI*/	case 6: pipeExectemp = pipeExectemp2 | pipeExecimm; break;
	
	 /*J*/  case 7: pc = (pc & 0b11110000000000000000000000000000) + pipeExecaddress; flagJBNE = true; break;
		 
	/*SLL*/ case 8: pipeExectemp = pipeExectemp2 << pipeExecshamt; break;
	
	/*SRL*/ case 9: pipeExectemp = pipeExectemp2 >>> pipeExecshamt; break;
		}
		
	}
	
	public static void memory() {
		
		switch(pipeMemopcode) {
	 /*LW*/ case 10: pipeMemtemp = (int) memory[pipeMemtemp2 + pipeMemimm]; break;

	 /*SW*/ case 11: memory[pipeMemtemp2 + pipeMemimm] = pipeMemtemp; break;  
		}
		
	}
	
	public static void writeback() {
		if(pipeWBr1 != 0) {
			switch(pipeWBopcode) {
				case 0: case 1: case 2: case 3: case 5: case 6: case 8: case 9: case 10: gprs[pipeWBr1] = pipeWBtemp; break;
			}
		}
		
	}
	
	
	
	public static void main(String[] args) {
		readProgram();
		int clkcycle = 1;
		int fn = 1;
		int dn = 1;
		int en = 1;
		int mn = 1;
		int wn = 1;
		boolean df = false;
		boolean ef = false;
		
		
		memory[1026] = 1000;
		
		
		/*Pipelined*/
		for(int i=0; i<(7+((programsize-1)*2)) ;i++)
		{
			
			System.out.println();
			System.out.println("Clock Cycle = " + clkcycle);
			System.out.println();
			if(df) {
				System.out.println("Decode instruction " + (dn-1));
				System.out.println("Decode Outputs: ");
				System.out.println("Opcode: " + opcode);
				System.out.println("RS: " + r1);					
				System.out.println("RT: " + r2);					
				System.out.println("RD: " + r3);					
				System.out.println("Shift Amount: " + shamt);					
				System.out.println("Immediate: " + imm);			
				System.out.println("Address: " + address);
				System.out.println();
				df = false;
			}
			if(ef) {
				System.out.println("Execute instruction " + (en-1));
				System.out.println("Execute Outputs: ");
				if(pipeExecopcode == 4 || pipeExecopcode == 7) {							
					System.out.println("PC: " + pipeJpc);
				}
				else if(pipeExecopcode != 10 && pipeExecopcode != 11)
					System.out.println("R" + pipeExecr1 + ": " + pipeExectemp);
				System.out.println();
				ef = false;
				if(flagJBNE == true) {
					removeins1 = en;
					ri1ex = 2;
					ri1mem = 4;
					ri1wb = 5;
					removeins2 = en+1;
					ri2de = 2;
					ri2ex = 4;
					ri2mem = 6;
					ri2wb = 7;
				}
			}

			if(clkcycle %2 == 1) {
				if(fn <= programsize) {
					if(memory[pc] != null) {
						fetch();
						System.out.println("Fetch instruction " + fn);
						System.out.println();
						fn ++;
						if(flagJBNE) {
							pc = pipeJpc;
							fn = pc;
							flagJBNE = false;
							flagFD = true;
						}
					}
				}
				if(clkcycle != 1) {
					pipeExecopcode = opcode;
					pipeExectemp = temp;
					pipeExectemp2 = temp2;
					pipeExectemp3 = temp3;
					pipeExecr1 = r1;
					pipeExecr2 = r2;
					pipeExecr3 = r3;
					pipeExecimm = imm;
					pipeExecaddress = address;
					pipeExecshamt = shamt;
					if(clkcycle != 3 && clkcycle != 5) {
						if(wn <= programsize) {
							if((removeins1 != wn || (removeins1 == wn && ri1wb <= 0)) && (removeins2 != wn || (removeins2 == wn && ri2wb <= 0))) {
								System.out.println("Write Back instruction " + wn);
								writeback();
								if(pipeWBopcode != 4 && pipeWBopcode != 7 && pipeWBopcode != 11) {
									System.out.println("Write Back Output: ");
									System.out.println("R" + pipeWBr1 + ": " + pipeWBtemp);
								}
								System.out.println();
							}
							wn ++;
							if(flagMW) {
								wn = mn;
								flagMW = false;
							}
						}
					}
				}
			}
			else if(clkcycle %2 == 0) {
				if(dn <= programsize) {
					if((removeins2 != dn || (removeins2 == dn && ri2de <= 0))) {
						decode();
						System.out.println("Decode instruction " + dn);
						System.out.println("Decode Inputs: ");
						System.out.println("Instruction: " + fetched_ins);
						System.out.println();
						df = true;
					}
					dn ++;
					if(flagFD) {
						dn = fn;
						flagFD = false;
						flagDE = true;
						c1 = 2;
					}
				}
				if(clkcycle != 2) {
					if(en <= programsize) {
						if((removeins1 != en || (removeins1 == en && ri1ex <= 0)) && (removeins2 != en || (removeins2 == en && ri2ex <= 0))) {
							System.out.println("Execute instruction " + en);
							System.out.println("Execute Inputs: ");
							if(pipeExecopcode == 0 || pipeExecopcode == 1 || pipeExecopcode == 8 || pipeExecopcode == 9) {
								System.out.println("Opcode: " + pipeExecopcode);
								System.out.println("R" + pipeExecr1 + ": " + pipeExectemp);
								System.out.println("R" + pipeExecr2 + ": " + pipeExectemp2);
								System.out.println("R" + pipeExecr3 + ": " + pipeExectemp3);							
								System.out.println("Shift Amount: " + pipeExecshamt);					
							}
							else if(pipeExecopcode == 7) {
								System.out.println("Opcode: " + pipeExecopcode);
								System.out.println("Address: " + pipeExecaddress);										
							}
							else {							
								System.out.println("Opcode: " + pipeExecopcode);
								System.out.println("R" + pipeExecr1 + ": " + pipeExectemp);
								System.out.println("R" + pipeExecr2 + ": " + pipeExectemp2);
								System.out.println("Immediate: " + pipeExecimm);			
							}
							pipetempc = pc;
							execute();
							pipeJpc = pc;
							pc = pipetempc;
							System.out.println();
							ef = true;
						}
						en ++;
						if(flagDE && c1 == 0) {
							en = dn-1;
							flagDE = false;
							flagEM = true;
							c2 = 2;
						}
					}
					if(clkcycle != 4) {
						if(mn <= programsize) {
							if((removeins1 != mn || (removeins1 == mn && ri1mem <= 0)) && (removeins2 != mn || (removeins2 == mn && ri2mem <= 0))) {
								System.out.println("Memory instruction " + mn);
								if(pipeMemopcode == 10) {
									System.out.println("Memory Inputs: ");
									int location = pipeMemtemp2 + pipeMemimm;
									System.out.println("Memory [" + location + "]: " + memory[pipeMemtemp2 + pipeMemimm]);
								}
								else if(pipeMemopcode == 11) {
									System.out.println("Memory Inputs: ");
									System.out.println("R" + pipeMemr1 + ": " + pipeMemtemp);								
								}
								memory();
								if(pipeMemopcode == 10) {
									System.out.println("Memory Outputs: ");
								}
								else if(pipeMemopcode == 11) {
									System.out.println("Memory Outputs: ");
									int location = pipeMemtemp2 + pipeMemimm;
									System.out.println("Memory [" + location + "]: " + memory[pipeMemtemp2 + pipeMemimm]);
								}
								System.out.println();
							}
							mn ++;
							if(flagEM && c2 == 0) {
								mn = en-1;
								flagEM = false;
								flagMW = true;
							}
						}
						pipeWBopcode = pipeMemopcode;
						pipeWBtemp = pipeMemtemp;
						pipeWBr1 = pipeMemr1;
					}
					pipeMemopcode = pipeExecopcode;
					pipeMemtemp = pipeExectemp;
					pipeMemtemp2 = pipeExectemp2;
					pipeMemr1 = pipeExecr1;
					pipeMemr2 = pipeExecr2;
					pipeMemimm = pipeExecimm;
				}
			}
			if(flagDE)
				c1--;
			if(flagEM)
				c2--;
			ri1ex --;
			ri1mem --;
			ri1wb --;
			ri2de --;
			ri2ex --;
			ri2mem --;
			ri2wb --;
			System.out.println();
			System.out.print("R[0]: ");
			System.out.println(gprs[0]);
			System.out.println();
			clkcycle++;
			System.out.println("--- CYCLE END ---");
		}
		System.out.println("--- PROGRAM END ---");
		
		System.out.println();
		
		System.out.println("PC " + pc);

		System.out.println();
		
		for(int i=0; i < gprs.length ;i++)
			System.out.println("R" + i + " " + gprs[i]);
		
		System.out.println();

		for(int i=0; i<memory.length ;i++) {
			if(i <= 1023) {
				System.out.println("Instruction Memory [" + i + "] " + memory[i]);
				if(i == 1023)
					System.out.println();
			}
			else
				System.out.println("Data Memory [" + i + "] " + memory[i]);
		}
		
		System.out.println();
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	

}