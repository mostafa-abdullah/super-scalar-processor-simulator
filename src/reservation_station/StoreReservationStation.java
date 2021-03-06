package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;

public class StoreReservationStation extends ReservationStation {
	private static int cycles;
	
	protected StoreReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new StoreReservationStation(false));

	}

	@Override
	public void issueInstruction(short instruction, short instructionAddress, short destROB) {
		super.issueInstruction(instruction, instructionAddress, destROB);
		super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		this.setAddress(InstructionDecoder.getImmediate(instruction));
	}

	@Override
	public void executeInstruction() {
		short newAddress = (short) (this.getVj() + this.getAddress());
		if(this.getQj() == READY){
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setDestination(newAddress);
		}
		
		this.incrementTimer();
		
		if(this.readyToWrite()) {
			this.setState(ReservationStationState.WRITE);
		}
	}

	@Override
	public void writeInstruction() {
		if(this.getQk() == READY){
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setValue(this.getVk());
			
			this.setState(ReservationStationState.COMMIT);
			this.clearBusy();
		}
	}
	
	public static void setCycles(int cycles) {
		StoreReservationStation.cycles = cycles;
	}
	
	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == StoreReservationStation.cycles;
	}
}
