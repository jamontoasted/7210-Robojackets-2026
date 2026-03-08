// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kMidScore = "Mid Score";
  private static final String kLScoreCollect = "Left Score / Collect";
  private static final String kRScoreCollect = "Right Score / Collect";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private final SparkMax leftDriveLead = new SparkMax(3,MotorType.kBrushed);
  private final SparkMax leftDriveFollow = new SparkMax(4,MotorType.kBrushed);
  private final SparkMax rightDriveLead = new SparkMax(1,MotorType.kBrushed);
  private final SparkMax rightDriveFollow = new SparkMax(2,MotorType.kBrushed);

  private final SparkMax intakeLauncher = new SparkMax(5, MotorType.kBrushless);
  private final SparkMax feeder = new SparkMax(6, MotorType.kBrushless);

  private final DifferentialDrive drive = new DifferentialDrive(leftDriveLead, rightDriveLead);

  private final Timer autoTimer = new Timer();
  private final Timer spinUpTimer = new Timer();

  private final XboxController controller = new XboxController(0);

// fuel system constants:
private static final double INTAKING_INTAKE_SPEED = 0.6;
private static final double INTAKING_FEEDER_SPEED = 1;

private static final double LAUNCHING_INTAKE_SPEED = 0.9;
private static final double LAUNCHING_FEEDER_SPEED = -1;
private static final double SLOW_LAUNCHING_INTAKE_SPEED = 0.7;
private static final double SPIN_UP_FEEDER_SPEED = 0.5;
private static final double SPIN_UP_SECONDS = 0.5;


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Middle Score", kMidScore);
    m_chooser.addOption("Left Score / Collect", kLScoreCollect);
    m_chooser.addOption("Right Score / Collect", kRScoreCollect);
    SmartDashboard.putData("Auto choices", m_chooser);

// drive configuration:
    SparkMaxConfig driveConfig = new SparkMaxConfig();
    driveConfig.voltageCompensation(12);
    driveConfig.smartCurrentLimit(60);

    driveConfig.follow(leftDriveLead);
    leftDriveFollow.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    driveConfig.follow(rightDriveLead);
    rightDriveFollow.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    driveConfig.disableFollowerMode();
    driveConfig.inverted(true);
    leftDriveLead.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    driveConfig.disableFollowerMode();
    driveConfig.inverted(false);
    rightDriveLead.configure(driveConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

// fuel motor configuration:
    SparkMaxConfig launcherConfig = new SparkMaxConfig();
    launcherConfig.smartCurrentLimit(60);
    launcherConfig.inverted(false);
    intakeLauncher.configure(launcherConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig feederConfig = new SparkMaxConfig();
    launcherConfig.smartCurrentLimit(60);
    feeder.configure(feederConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    autoTimer.start();
    autoTimer.reset();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kDefaultAuto:
      default:
        // Put default auto code here
        if(autoTimer.get() < 1){
        drive.arcadeDrive(0.5, 0);
        }
        else{
          drive.arcadeDrive(0, 0);
        }
        break;

      case kMidScore:
        // Put custom auto code here
      if (autoTimer.get() < 1){
        drive.arcadeDrive(.5, 0);
        intakeLauncher.set(SLOW_LAUNCHING_INTAKE_SPEED);
        feeder.set(SPIN_UP_FEEDER_SPEED);
      }
      else if (autoTimer.get() < 10){
        drive.arcadeDrive(0, 0);
        intakeLauncher.set(LAUNCHING_INTAKE_SPEED);
        feeder.set(LAUNCHING_FEEDER_SPEED);
      }
      else {
        drive.arcadeDrive(0, 0);
        intakeLauncher.set(0);
        feeder.set(0);
      }
        break;

      case kLScoreCollect:
      if (autoTimer.get() < 0.8){
        drive.arcadeDrive(0, 0);
        intakeLauncher.set(SLOW_LAUNCHING_INTAKE_SPEED);
        feeder.set(SPIN_UP_FEEDER_SPEED);
      }
      else if (autoTimer.get() < 9){
        drive.arcadeDrive(0, 0);
        intakeLauncher.set(SLOW_LAUNCHING_INTAKE_SPEED);
        feeder.set(LAUNCHING_FEEDER_SPEED);
      }
      else if (autoTimer.get() < 9 + .8){
        drive.arcadeDrive(.5, 0);
        intakeLauncher.set(0);
        feeder.set(0);
      }
      else if (autoTimer.get() < 9 + .8 + .8){
        drive.arcadeDrive(0, 0.5);
        intakeLauncher.set(0);
        feeder.set(0);
      }
      else if (autoTimer.get() < 8.8 + .8 + 8){
        drive.arcadeDrive(0.8, 0);
        intakeLauncher.set(INTAKING_INTAKE_SPEED);
        feeder.set(INTAKING_FEEDER_SPEED);
      }
      else {
        drive.arcadeDrive(0, 0);
        intakeLauncher.set(0);
        feeder.set(0);
      }

    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    autoTimer.stop();
    spinUpTimer.start();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if (controller.getRightStickButton()){
    drive.arcadeDrive(-controller.getLeftY(), -controller.getRightX() * .5);
    }
    else {
      drive.arcadeDrive(-controller.getLeftY(), -controller.getRightX() * .8);
    }

      if (controller.getLeftTriggerAxis() > 0.2){// Intake
        intakeLauncher.set(INTAKING_INTAKE_SPEED);
        feeder.set(INTAKING_FEEDER_SPEED);
      }
    else if (controller.getRightBumperButton()){// Launch
      if(controller.getRightBumperButtonPressed()){
        spinUpTimer.reset();
      }
      if (spinUpTimer.get() < SPIN_UP_SECONDS){
        intakeLauncher.set(LAUNCHING_INTAKE_SPEED);
        feeder.set(SPIN_UP_FEEDER_SPEED);
      }
      else {
        intakeLauncher.set(LAUNCHING_INTAKE_SPEED);
        feeder.set(LAUNCHING_FEEDER_SPEED);
      }
    }
    else if (controller.getLeftBumperButton()){// Slow launch
      if(controller.getLeftBumperButtonPressed()){
        spinUpTimer.reset();
      }
      if (spinUpTimer.get() < SPIN_UP_SECONDS){
        intakeLauncher.set(SLOW_LAUNCHING_INTAKE_SPEED);
        feeder.set(SPIN_UP_FEEDER_SPEED);
      }
      else {
        intakeLauncher.set(SLOW_LAUNCHING_INTAKE_SPEED);
        feeder.set(LAUNCHING_FEEDER_SPEED);
      }
    }
    else if (controller.getAButton()){// Eject
      intakeLauncher.set(-INTAKING_INTAKE_SPEED);
      feeder.set(-INTAKING_FEEDER_SPEED);
    }
    else {// Off
      intakeLauncher.set(0);
      feeder.set(0);
    }

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
